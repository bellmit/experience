package com.excenergy.tagdataserv.log;

import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.mem.TVMemFillListener;
import com.excenergy.tagdataserv.mem.TVMemItem;
import com.typesafe.config.Config;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.excenergy.tagdataserv.Utils.timeFormat;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-06
 */
public class WriteLogThread implements Runnable, TVMemFillListener {
    private static final Logger logger = LoggerFactory.getLogger(WriteLogThread.class);

    private final BlockingQueue<Integer> handleQueue;
    private final BlockingQueue<TVMemItem> itemQueue;
    private final Path basePath;

    private final int logFileSize;
    private final int flushCycle;
    private final int flushCount;
    private final int queueWarningSize;
    private final BlockingQueue<PersistTask> persistQueue;
    private final PersistThread persistThread;
    private final Application app;

    private boolean useNewCache;

    public WriteLogThread(Application app) {
        this.app = app;
        TVLog log = app.getTvLog();
        handleQueue = log.handleQueue;
        itemQueue = log.itemQueue;
        basePath = log.basePath;

        this.persistThread = app.getPersistThread();
        this.persistQueue = persistThread.getPersistQueue();

        Config config = app.getConfig();
        logFileSize = config.getInt("log.size") * 1024 * 1024;// 单位为M
        flushCycle = config.getInt("log.flush_cycle") * DateTimeConstants.MILLIS_PER_SECOND; // 单位为秒
        flushCount = config.getInt("log.flush_count");
        queueWarningSize = config.getInt("log.queue_warning_size");
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Write-Log-Thread");
        if (logger.isInfoEnabled()) {
            logger.info("The write thread has started.");
        }

        String fileName = null; // 文件名，用该文件存储的第1个实时值的Version
        FileChannel writeChannel = null;
        long lastForceTime = System.currentTimeMillis();
        int count = 0;

        HashMap<Integer, Set<TV>> map = null; // 索引日志文件中的内容，因内存库中需要存储，所以这里只占用索引的空间
        List<Integer> handleList = new ArrayList<>();
        List<TVMemItem> itemList = new ArrayList<>();
        while (app.getStatus() != Application.AppStatus.Closed) {
            try {
                int size = itemQueue.drainTo(itemList);
                if (size < 1) {
                    TimeUnit.SECONDS.sleep(1);
                    continue;
                }
                handleQueue.drainTo(handleList, size);
                if (size > queueWarningSize) {
                    if (logger.isWarnEnabled()) {
                        logger.warn(String.format("Now has %d tasks need to persist, may be something wrong.", size));
                    }
//                    String code = "DATA-000001";
//                    String msg = TagDataException.getMsg(code);
//                    msgQueue.append(new WarnMsg(code, "TagDataServer.log.WriteLogThread", msg, new Date()));
                }

                if (fileName == null || writeChannel == null || map == null) { // 新启用一个文件
                    fileName = String.valueOf(itemList.get(0).getVersion());
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("New file name:%s", fileName));
                    }
                    File dir = basePath.toFile();
                    if (!dir.exists()) {
                        boolean mkdir = dir.mkdirs();
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Create base dir:%s, %b", dir.getName(), mkdir));
                        }
                    }
                    writeChannel = FileChannel.open(basePath.resolve(fileName), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("WriteChannel isOpen:%b", writeChannel.isOpen()));
                    }
                    map = new HashMap<>();
                }

                writeChannel.write(encode(map, handleList, itemList));
                count += size;

                // 是否需要强制刷到磁盘
                long l = System.currentTimeMillis();
                if (l - lastForceTime > flushCycle || count >= flushCount) {
                    writeChannel.force(true);
                    lastForceTime = l;
                    count = 0;
                }

                if (useNewFile(writeChannel.size())) {
                    // 文件已经写满，或 内存库已满
                    int latestVersion = itemList.get(itemList.size() - 1).getVersion();
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Now persist file%s, version:%d. useNewCache:%b, writeChannel.size:%d, ", fileName, latestVersion, useNewCache(), writeChannel.size()));
                    }
                    writeChannel.close();
                    if (!persistQueue.offer(new PersistTask(fileName, latestVersion, map))) {
                        logger.error("Offer persist task fail.");
                    }
                    writeChannel = null;
                    map = null;
                    fileName = null;
                    useNewCache(false);
                }

                handleList.clear();
                itemList.clear();
            } catch (Throwable e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Something unexpected occurred.", e);
                }
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Shutdown service, persist the data in file. Current time:%s", timeFormat(System.currentTimeMillis())));
        }
        // 关闭时，持久化
        if (!persistQueue.offer(new PersistTask(fileName, -1, map))) {
            logger.error("Offer persist task fail.");
        }
    }

    private ByteBuffer encode(HashMap<Integer, Set<TV>> map, List<Integer> handleList, List<TVMemItem> itemList) {
        ArrayList<ByteBuffer> bfList = new ArrayList<>(handleList.size());
        int total = 0;
        for (int i = 0; i < handleList.size(); i++) {
            Integer handle = handleList.get(i);
            TVMemItem item = itemList.get(i);

            if (map.get(handle) == null) {
                map.put(handle, new HashSet<TV>());
            }
            Set<TV> tvs = map.get(handle);
            TV tv = item.getTv();
            if (tvs.contains(tv)) {
                tvs.remove(tv);
            }
            tvs.add(tv);

            ByteBuffer bf = LogMapper.encode(handle, tv);
            total += bf.remaining();
            bfList.add(bf);
        }

        ByteBuffer buffer = ByteBuffer.allocate(total);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        for (ByteBuffer bf : bfList) {
            buffer.put(bf);
        }
        buffer.rewind();
        return buffer;
    }

    private boolean useNewFile(long fileSize) {
        if (persistThread.isPersisting()) { // 如果正在持久化，则不触发，保证只有一个持久化线程在执行
            if (fileSize > logFileSize) {
                logger.warn("Current file is reach config max size, but the prev persist has not finished.");
            }
            if (useNewCache()) {
                logger.warn("Memory is fill, now the prev persist has not finished.");
            }
        }

        return fileSize > logFileSize || useNewCache();
    }

    @Override
    public void memFill() {
        // 内存库满了，触发持久化，仅在当前未进行持久化时触发
        // 原因：正在持久化的数据，是造成内存满的原因，当持久化结束，内存库中的数据便可以删掉
        if (!persistThread.isPersisting()) {
            useNewCache(true);
        }
    }

    private synchronized void useNewCache(boolean b) {
        if (useNewCache != b) {
            useNewCache = b;
            if (logger.isInfoEnabled()) {
                logger.info(String.format("useNewCache:%b", b));
            }
        }
    }

    private synchronized boolean useNewCache() {
        return useNewCache;
    }
}
