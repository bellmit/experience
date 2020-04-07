package com.excenergy.tagdataserv.stats;

import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagdataserv.mem.TVMemPool;
import com.excenergy.tagdataserv.net.LoginHandler;
import com.excenergy.tagmeta.RealTag;
import com.excenergy.tagmeta.Tag;
import com.typesafe.config.Config;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static com.excenergy.tagdataserv.disk.DefaultDB.getDiskReadBytes;
import static com.excenergy.tagdataserv.disk.DefaultDB.getDiskWriteBytes;
import static com.excenergy.tagdataserv.log.PersistThread.getLogPersistCount;
import static com.excenergy.tagdataserv.mem.TVMemPool.getMemReadTVCount;
import static com.excenergy.tagdataserv.mem.TVMemPool.getTotalReadTVCount;
import static com.excenergy.tagdataserv.net.CmdDataDecoder.getNetReadBytes;
import static com.excenergy.tagdataserv.net.CmdDataEncoder.getNetWriteBytes;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-09
 */
public class StatsItems {
    private static final Logger logger = LoggerFactory.getLogger(StatsItems.class);

    public static final int CLIENT_COUNT = 1;
    public static final int THREAD_COUNT = 2;
    public static final int NET_THROUGHPUT = 3;
    public static final int MEM_USAGE = 4;
    public static final int MEM_HIT = 5;
    public static final int MEM_TV_COUNT = 6;
    public static final int AVERAGE_PERSIST_INTERVAL = 7;
    public static final int DISK_THROUGHPUT = 8;
    public static final int DISK_USAGE = 9;

    private final TVMemPool memPool;
    private final Vector<LoginHandler.Session> clientSessions;
    private final Long onlineTime;
    private final String dataPath;

    public long netWriteBytes; // 网络写入量，用来算网络吞吐量
    public long netReadBytes; // 网络写出量，用来算网络吞吐量

    public long diskReadBytes; // 读磁盘字节数，用来算磁盘IO吞吐量
    public long diskWriteBytes; // 写磁盘字节数，用来算磁盘IO吞吐量

    private int period; // 标识多长时间触发一次统计

    public StatsItems(Application app) {
        Config config = app.getConfig();
        TagFactory tagFactory = app.getTagFactory();
        this.memPool = app.getMemPool();
        this.clientSessions = app.getClientSessions();
        this.onlineTime = app.getOnlineTime();
        period = config.getInt("stats.period");
        tagFactory.put(createTag(CLIENT_COUNT, "r.client_count", Tag.INT));
        tagFactory.put(createTag(THREAD_COUNT, "r.thread_count", Tag.INT));
        tagFactory.put(createTag(NET_THROUGHPUT, "r.net_throughput", Tag.INT));
        tagFactory.put(createTag(MEM_USAGE, "r.mem_usage", Tag.LONG));
        tagFactory.put(createTag(MEM_HIT, "r.mem_hit", Tag.DOUBLE));
        tagFactory.put(createTag(MEM_TV_COUNT, "r.mem_tv_count", Tag.INT));
        tagFactory.put(createTag(AVERAGE_PERSIST_INTERVAL, "r.average_persist_interval", Tag.INT));
        tagFactory.put(createTag(DISK_THROUGHPUT, "r.disk_throughput", Tag.INT));
        tagFactory.put(createTag(DISK_USAGE, "r.disk_usage", Tag.LONG));

        dataPath = config.getString("application.data_archive_path");
    }

    private RealTag createTag(int handle, String name, byte valType) {
        RealTag tag = new RealTag(handle, name);
        tag.setValType(valType);
        tag.setCacheMax(50);
        tag.setMergeInterval(Tag.HOURLY);
        tag.setTolerance(period * 2);

        tag.setEnableCompress(false);
        tag.setCompressAcc(300);
        tag.setPrInterval(900);

        tag.setPublic(false);
        tag.setPersist(false);
        tag.setCumulativeVal(false);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Create Tag:%s", tag));
        }
        return tag;
    }

    public Map<Integer, TagValue> getStats() {
        Map<Integer, TagValue> stats = new HashMap<>();
        DateTime now = DateTime.now();
        stats.put(CLIENT_COUNT, new TagValue(now, Tag.INT, getClientCount(), (short) 192));
        stats.put(THREAD_COUNT, new TagValue(now, Tag.INT, getThreadCount(), (short) 192));
        stats.put(NET_THROUGHPUT, new TagValue(now, Tag.INT, getNetThroughput(), (short) 192));
        stats.put(MEM_USAGE, new TagValue(now, Tag.LONG, getMemUsage(), (short) 192));
        stats.put(MEM_HIT, new TagValue(now, Tag.DOUBLE, getMemHit(), (short) 192));
        stats.put(MEM_TV_COUNT, new TagValue(now, Tag.INT, getMemTVCount(), (short) 192));
        stats.put(AVERAGE_PERSIST_INTERVAL, new TagValue(now, Tag.INT, getAveragePersistInterval(), (short) 192));
        stats.put(DISK_THROUGHPUT, new TagValue(now, Tag.INT, getDiskThroughput(), (short) 192));
        stats.put(DISK_USAGE, new TagValue(now, Tag.LONG, getDiskUsage(), (short) 192));
        return stats;
    }

    /**
     * API客户端的数量
     *
     * @return API客户端的个数
     */
    private int getClientCount() {
        int clientCount = clientSessions.size();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Client count:%d", clientCount));
        }
        return clientCount;
    }

    /**
     * 提供服务的线程数
     *
     * @return
     */
    private int getThreadCount() {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Thread count:%d", 0));
        }
        return 0; // todo 依赖网络模块
    }

    /**
     * 网络吞吐量，网络写入与写出字节数 除以 时间
     *
     * @return 单位： 字节每秒
     */
    private int getNetThroughput() {
        long write = getNetWriteBytes();
        long read = getNetReadBytes();
        long throughput = (write + read - netWriteBytes - netReadBytes) / period;
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Net throughput:%d", throughput));
        }
        netReadBytes = read;
        netWriteBytes = write;
        return (int) throughput;
    }

    /**
     * 内存使用量
     *
     * @return
     */
    private long getMemUsage() {
        Runtime runtime = Runtime.getRuntime();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Mem usage:%d", runtime.totalMemory()));
        }
        return runtime.totalMemory();
    }

    /**
     * 内存库命中率，是从内存库中读的TV个数除以总的返回个数
     *
     * @return 小于1的小数
     */
    private double getMemHit() {
        if (getTotalReadTVCount() == 0) {
            return 1;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Mem hit:%d", getMemReadTVCount() / getTotalReadTVCount()));
        }
        return (getMemReadTVCount() * 1.0) / getTotalReadTVCount();
    }

    private int getMemTVCount() {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Mem TV Count:%d", memPool.getMemTVCount()));
        }
        return memPool.getMemTVCount();
    }

    /**
     * 平均持久化时间，系统启动后的时间除以持久化次数
     *
     * @return 单位为秒
     */
    private int getAveragePersistInterval() {
        if (getLogPersistCount() == 0) {
            return 0;
        }
        long runningTime = (System.currentTimeMillis() - onlineTime) / DateTimeConstants.MILLIS_PER_SECOND;
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Average Persist Interval:%d", (int) (runningTime / getLogPersistCount())));
        }
        return (int) (runningTime / getLogPersistCount());
    }

    /**
     * 磁盘吞吐量，磁盘读写字节数除以时间
     *
     * @return 单位： 字节每秒
     */
    private synchronized int getDiskThroughput() {
        long read = getDiskReadBytes();
        long write = getDiskWriteBytes();
        int throughput = (int) ((read + write - diskWriteBytes - diskReadBytes) / period);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Disk throughput:%d", throughput));
        }
        diskReadBytes = read;
        diskWriteBytes = write;
        return throughput;
    }

    /**
     * 磁盘使用量
     *
     * @return 磁盘使用的字节数
     */
    private long getDiskUsage() {
        long dirSize = getDirSize(new File(dataPath));
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Disk usage:%d", dirSize));
        }
        return dirSize;
    }

    private long getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children != null && children.length > 0) {
                    long size = 0;
                    for (File f : children) {
                        size += getDirSize(f);
                    }
                    return size;
                } else {
                    return 0;
                }
            } else {
                return file.length();
            }
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("file not exist.");
            }
            return 0;
        }
    }
}
