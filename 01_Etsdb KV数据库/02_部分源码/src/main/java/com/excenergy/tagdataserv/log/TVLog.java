package com.excenergy.tagdataserv.log;

import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.mem.TVMemItem;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-20
 */
public class TVLog {
    private static final Logger logger = LoggerFactory.getLogger(TVLog.class);

    BlockingQueue<Integer> handleQueue = new LinkedBlockingDeque<>();
    BlockingQueue<TVMemItem> itemQueue = new LinkedBlockingDeque<>();

    Path basePath;
    private int blockSize;
    private BlockingQueue<PersistTask> persistQueue;

    public TVLog(Application app) {
        Config config = app.getConfig();
        this.basePath = Paths.get(config.getString("application.data_log_path"));
        this.blockSize = config.getInt("log.block") * 1024;
    }

    public synchronized void append(int handle, TVMemItem item) {
        if (item == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "TVMemItem", "TVLog.append");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }

        if (handleQueue.offer(handle)) {
            if (!itemQueue.offer(item)) {
                handleQueue.remove(handle);
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Add Item:%s to Queue error", item));
                }
            }
        } else {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Add handle:%d to Queue error", handle));
            }
        }

        if (persistQueue.size() > 0) {
            try {
                // 当持久化慢，进行不下去的时候，为防止内存库爆满，减慢写入速度
                TimeUnit.MILLISECONDS.sleep(persistQueue.size());
            } catch (InterruptedException e) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Add handle:%d to Queue error", handle));
                }
            }
        }
    }

    /**
     * 启动加载时，所有日志文件全部入历史库，并加载到内存中
     *
     * @return Key为handle，Value为TV的List，如果文件找不到，返回空的Map
     */
    public Map<Integer, Set<TV>> load() {
        if (logger.isInfoEnabled()) {
            logger.info("load TV in log file.");
        }
        Map<Integer, Set<TV>> result = new HashMap<>();
        File[] files = basePath.toFile().listFiles();
        if (files == null) {
            if (logger.isInfoEnabled()) {
                logger.info("The log file is not exist.");
            }
            return result;
        }

        for (File file : files) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("asynchronous persist file:%s", file.getName()));
                }
                Map<Integer, Set<TV>> logTVs = read(file.getName());
                if (!persistQueue.offer(new PersistTask(file.getName(), -1, logTVs))) {
                    logger.error("offer task fail.");
                }
                result.putAll(logTVs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Map<Integer, Set<TV>> read(String fileName) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Reading file:%s", fileName));
        }
        Map<Integer, Set<TV>> result = new HashMap<>();
        FileChannel channel = FileChannel.open(basePath.resolve(fileName), StandardOpenOption.READ);

        ByteBuffer bf = ByteBuffer.allocate(0);
        // 读一个Block
        ByteBuffer block = ByteBuffer.allocate(blockSize);
        block.order(ByteOrder.LITTLE_ENDIAN);
        int read = channel.read(block);
        while (read > 0) { // 未到文件末尾
            block.limit(read);
            block.rewind();
            // 新的buffer包括上一个Block不够一条记录的 和 下一个块
            bf = ByteBuffer.allocate(bf.remaining() + read).put(bf).put(block);
            bf.order(ByteOrder.LITTLE_ENDIAN);
            bf.rewind();

            Map<Integer, Set<TV>> decode = LogMapper.decode(bf);
            for (Map.Entry<Integer, Set<TV>> entry : decode.entrySet()) {
                if (result.get(entry.getKey()) == null) {
                    result.put(entry.getKey(), entry.getValue());
                } else {
                    result.get(entry.getKey()).addAll(entry.getValue());
                }
            }

            // 读一个Block
            block.clear();
            read = channel.read(block);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Reach the end of file.");
        }
        channel.close();
        return result;
    }

    public void setPersistQueue(BlockingQueue<PersistTask> persistQueue) {
        this.persistQueue = persistQueue;
    }
}
