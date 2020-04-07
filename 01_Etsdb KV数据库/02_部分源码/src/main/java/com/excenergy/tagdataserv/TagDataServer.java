package com.excenergy.tagdataserv;

import com.excenergy.ecomm.config.TypesafeConfigWrapper;
import com.excenergy.emq.MsgConfig;
import com.excenergy.emq.MsgQueueService;
import com.excenergy.protocol.CommandData;
import com.excenergy.tagdataserv.disk.TVDB;
import com.excenergy.tagdataserv.disk.TVDBFactory;
import com.excenergy.tagdataserv.log.PersistTask;
import com.excenergy.tagdataserv.log.PersistThread;
import com.excenergy.tagdataserv.log.TVLog;
import com.excenergy.tagdataserv.log.WriteLogThread;
import com.excenergy.tagdataserv.mem.TVMemItem;
import com.excenergy.tagdataserv.mem.TVMemPool;
import com.excenergy.tagdataserv.net.*;
import com.excenergy.tagdataserv.stats.StatsCollector;
import com.excenergy.tagdataserv.stats.StatsItems;
import com.excenergy.tagdataserv.telnet.StatsCommand;
import com.excenergy.tagdataserv.telnet.TagFactoryCommand;
import com.excenergy.tagdataserv.telnet.TagStoreCommand;
import com.excenergy.telnetutil.TelnetServer;
import com.excenergy.telnetutil.security.SimpleAuthenticator;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-21
 */
public class TagDataServer implements TagChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(TagDataServer.class);
    private static TagDataServer ourInstance = new TagDataServer();

    private final Config config = ConfigFactory.load();
    private final Vector<AgentLoginHandler.Session> agentSessions = new Vector<>();
    private final Vector<LoginHandler.Session> clientSessions = new Vector<>();

    TagFactory tagFactory = TagFactory.getInstance();
    private StatsItems statsItems;
    private TVLog tvLog;
    private TagStorePool tagStorePool;
    private Application app;
    private TVMemPool memPool;

    private TagDataServer() {
    }

    public static TagDataServer getInstance() {
        return ourInstance;
    }

    public void start() {
        if (logger.isInfoEnabled()) {
            logger.info("Service is starting, please wait...");
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        long onlineTime = formatter.parseDateTime(config.getString("application.online")).getMillis();
        app = new Application();
        app.setStatus(Application.AppStatus.Init);
        app.setConfig(config);
        app.setOnlineTime(onlineTime);
        app.setTagFactory(tagFactory);
        app.setClientSessions(clientSessions);
        app.setAgentSessions(agentSessions);
        app.setThreadPool(Executors.newCachedThreadPool());
        app.setScheduledPool(Executors.newScheduledThreadPool(10));

        MsgConfig config = new MsgConfig();
        config.setAppConfig(TypesafeConfigWrapper.load());
        MsgQueueService msgQueueService = config.msgQueueService();
        app.setMsgQueueService(msgQueueService);
        if (logger.isInfoEnabled()) {
            logger.info("Create msg queue service success.");
        }

        TVDB tvdb = TVDBFactory.createTVDB(app);
        app.setTvdb(tvdb);

        memPool = new TVMemPool(app);
        app.setMemPool(memPool);

        tvLog = new TVLog(app);
        app.setTvLog(tvLog);

        tagStorePool = new TagStorePool(app);
        app.setTagStorePool(tagStorePool);

        statsItems = new StatsItems(app);
        app.setStatsItems(statsItems);

        tagFactory.addTagChangeListener(this);

        PersistThread persistThread = new PersistThread(app);
        app.getScheduledPool().scheduleWithFixedDelay(persistThread, 1, 1, TimeUnit.SECONDS);
        app.setPersistThread(persistThread);

        WriteLogThread writeLogThread = new WriteLogThread(app);
        app.setWriteLogThread(writeLogThread);

        // 启动写缓存线程
        app.getThreadPool().execute(writeLogThread);

        BlockingQueue<PersistTask> persistQueue = persistThread.getPersistQueue();
        tvLog.setPersistQueue(persistQueue);

        memPool.addMemFillListener(writeLogThread);

        startTelnet();

        PingSender pingSender = new PingSender(app);
        app.setPingSender(pingSender);

        pingSender.start();

        startTagMetaNetService();
        startTagCalculateNetService();
        app.setStatus(Application.AppStatus.WaitingTagMeta);
        if (logger.isInfoEnabled()) {
            logger.info("Waiting tag meta data...");
        }
    }

    private void startTelnet() {
        try {
            int telnetPort = config.getInt("telnet.port");
            TelnetServer telnet = new TelnetServer(telnetPort, "RTDB", config.getInt("telnet.max_client"));
            telnet.setGreeting("Welcome to RTDB"); // 欢迎词
            telnet.setAuthenticator(new SimpleAuthenticator(config.getString("telnet.login_password"))); // 登录密码
            telnet.setAdminPassword(config.getString("telnet.admin_password")); // 管理员密码

            telnet.addCommandHandler(new TagFactoryCommand(tagFactory));
            telnet.addCommandHandler(new TagStoreCommand(tagStorePool));
            telnet.addCommandHandler(new StatsCommand(app));
            telnet.start();

            if (logger.isInfoEnabled()) {
                logger.info(String.format("Listening telnet on %d", telnetPort));
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("Start telnet error", e);
            }
        }
    }

    @Override
    public void tagReady() {
        if (logger.isInfoEnabled()) {
            logger.info("Tag Meta data is ready. ");
        }
        loadLogCache();
        startNetService();
        startStatsCollector();
        app.setStatus(Application.AppStatus.Working);
        if (logger.isInfoEnabled()) {
            logger.info("##############################");
            logger.info("## Tag Data Server started. ##");
            logger.info("##      ^_^        ^_^      ##");
            logger.info("##############################");
        }
    }

    @Override
    public void tagChanged() {
        Channel tagCalculate = app.getTagCalculate();
        if (tagCalculate != null) {
            tagCalculate.writeAndFlush(new CommandData(CommandData.TAG_META_CHANGED, CommandData.VERSION_1, new byte[]{}));
        }
    }

    private void startTagMetaNetService() {
        if (logger.isInfoEnabled()) {
            logger.info("Starting net service for tag meta.");
        }
        int tagMetaPort = config.getInt("net.port.tagMeta");
        app.getThreadPool().submit(new StartNetServiceTask(StartNetServiceTask.TAG_META, tagMetaPort));
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Listening Tag Meta Service on %d", tagMetaPort));
        }
    }

    private void startTagCalculateNetService() {
        if (logger.isInfoEnabled()) {
            logger.info("Starting net service for tag Calculate.");
        }
        int tagCalculatePort = config.getInt("net.port.tagCalculate");
        app.getThreadPool().submit(new StartNetServiceTask(StartNetServiceTask.TAG_CALCULATE, tagCalculatePort));
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Listening Tag Calculate Service on %d", tagCalculatePort));
        }
    }

    private void startNetService() {
        if (logger.isInfoEnabled()) {
            logger.info("Starting net service.");
        }

        int apiPort = config.getInt("net.port.api");
        app.getThreadPool().submit(new StartNetServiceTask(StartNetServiceTask.API, apiPort));

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Listening Tag API Service on %d", apiPort));
        }

        int kernelPort = config.getInt("net.port.kernel");
        app.getThreadPool().submit(new StartNetServiceTask(StartNetServiceTask.KERNEL, kernelPort));

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Listening Tag Kernel Service on %d", kernelPort));
        }
    }

    private void loadLogCache() {
        if (logger.isInfoEnabled()) {
            logger.info("Loading TV from log...");
        }
        Map<Integer, Set<TV>> load = tvLog.load();
        int count = 0;
        for (Map.Entry<Integer, Set<TV>> entry : load.entrySet()) {
            Integer handle = entry.getKey();
            Set<TV> tvs = entry.getValue();
            for (TV tv : tvs) {
                memPool.put(handle, new TVMemItem(tv));
                count++;
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Load TV from log success. %d TVs has been loaded.", count));
        }
    }

    private void startStatsCollector() {
        // 启动统计信息采集线程
        if (logger.isInfoEnabled()) {
            logger.info("Starting stats collector");
        }
        if (config.getBoolean("stats.running")) {
            int period = config.getInt("stats.period");
            app.getScheduledPool().scheduleAtFixedRate(new StatsCollector(tagStorePool, statsItems), period, period, TimeUnit.SECONDS);
        }
        if (logger.isInfoEnabled()) {
            logger.info("Start stats collector success.");
        }
    }

    private class StartNetServiceTask implements Runnable {
        private static final int API = 0;
        private static final int KERNEL = 1;
        private static final int TAG_META = 2;
        private static final int TAG_CALCULATE = 3;

        private final int service;
        private final int port;

        public StartNetServiceTask(int tagMeta, int port) {
            this.service = tagMeta;
            this.port = port;
        }

        @Override
        public void run() {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup) //
                        .channel(NioServerSocketChannel.class) //
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                //                                if (!isChannelInitializer) {
                                ChannelPipeline pipeline = ch.pipeline();
                                List<ChannelHandler> handlers;
                                switch (service) {
                                    case API:
                                    default:
                                        handlers = apiHandler();
                                        break;
                                    case KERNEL:
                                        handlers = kernelHandler();
                                        break;
                                    case TAG_META:
                                        handlers = tagMetaHandler();
                                        break;
                                    case TAG_CALCULATE:
                                        handlers = tagCalculateHandler();
                                        break;
                                }
                                for (ChannelHandler handler : handlers) {
                                    pipeline.addLast(handler);
                                }
                            }
                        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
                b.childOption(ChannelOption.TCP_NODELAY, true);

                // Bind and start to accept incoming connections.
                ChannelFuture f = b.bind(port).sync();

                // Wait until the server socket is closed.
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }

        private List<ChannelHandler> apiHandler() {
            List<ChannelHandler> apiHandles = new ArrayList<>();
            apiHandles.add(new LoggingHandler());
            apiHandles.add(new CmdDataEncoder());
            apiHandles.add(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 4, 4, 6, 0, true));
            apiHandles.add(new CmdDataDecoder());
            apiHandles.add(new LoginHandler(app));
            apiHandles.add(new EnumAllHandler(tagFactory));
            apiHandles.add(new EnumSourceHandler(tagFactory));
            apiHandles.add(new EnumDeviceHandler(tagFactory));
            apiHandles.add(new EnumTagHandler(tagFactory));
            apiHandles.add(new ReadTagHandler(tagFactory));
            apiHandles.add(new WriteValHandler(tagStorePool));
            apiHandles.add(new ReadRealHandler(tagStorePool));
            apiHandles.add(new ReadHisHandler(tagStorePool));
            apiHandles.add(new ReadHisIntervalHandler(tagStorePool));
            apiHandles.add(new WriteTagHandler(tagFactory, tagStorePool, agentSessions));
            return apiHandles;
        }

        private List<ChannelHandler> kernelHandler() {
            List<ChannelHandler> kernelHandles = new ArrayList<>();
            kernelHandles.add(new LoggingHandler());
            kernelHandles.add(new CmdDataEncoder());
            kernelHandles.add(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 4, 4, 6, 0, true));
            kernelHandles.add(new CmdDataDecoder());
            kernelHandles.add(new AgentLoginHandler(app));
            kernelHandles.add(new EnumDeviceHandler(tagFactory));
            kernelHandles.add(new EnumTagHandler(tagFactory));
            kernelHandles.add(new WriteValHandler(tagStorePool));
            kernelHandles.add(new HisDataFinishHandler(app));
            return kernelHandles;
        }

        private List<ChannelHandler> tagMetaHandler() {
            List<ChannelHandler> handles = new ArrayList<>();
            handles.add(new LoggingHandler());
            handles.add(new CmdDataEncoder());
            handles.add(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 4, 4, 6, 0, true));
            handles.add(new CmdDataDecoder());
            handles.add(new TagMetaHandler(tagFactory));
            return handles;
        }

        private List<ChannelHandler> tagCalculateHandler() {
            List<ChannelHandler> handles = new ArrayList<>();
            handles.add(new LoggingHandler());
            handles.add(new CmdDataEncoder());
            handles.add(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 4, 4, 6, 0, true));
            handles.add(new CmdDataDecoder());
            handles.add(new TagCalculateHandler(app));
            return handles;
        }
    }
}
