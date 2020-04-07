package com.excenergy.tagdataserv.log;

import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.disk.TVDB;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-10-01
 */
public class PersistThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(PersistThread.class);
    private static final int SEMAPHORE_PERMITS = Integer.MAX_VALUE;

    private static int logPersistCount; // 持久化次数

    private final Semaphore semaphore = new Semaphore(SEMAPHORE_PERMITS);

    private final TVDB tvdb;
    private final TVLog log;

    private final BlockingQueue<PersistTask> persistQueue = new LinkedBlockingDeque<>();
    private final Path logBackupPath;

    public PersistThread(Application app) {
        this.log = app.getTvLog();
        this.tvdb = app.getTvdb();
        this.logBackupPath = Paths.get(app.getConfig().getString("application.log_backup_path"));
    }

    @Override
    public void run() {
        while (!persistQueue.isEmpty()) {
            try {
                persist();
            } catch (Throwable e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Something unexcepted error", e);
                }
            }
        }
    }

    void persist() throws InterruptedException {
        if (persistQueue.size() > 1) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Now has %d tasks need to persist, may be something wrong.", persistQueue.size()));
            }
//            String code = "DATA-000002";
//            String msg = TagDataException.getMsg(code);
//            msgQueue.append(new WarnMsg(code, "TagDataServer.log.PersistThread", msg, new Date()));
        }
        semaphore.acquireUninterruptibly();
        PersistTask task = persistQueue.take();
        String fileName = task.getFileName();
        int version = task.getVersion();
        Map<Integer, Set<TV>> tvMap = task.getTvMap();

        if (logger.isInfoEnabled()) {
            logger.info(String.format("Starting a thread to execute PersistTask, fileName:%s, version:%d", fileName, version));
        }

        try {
            tvdb.put(tvMap);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Persist error", e);
            }
            return;
        }

        remove(fileName);

        if (version > 0) {
            LatestPersist.setVersion(version);
        }
        PersistThread.addPersistCount();
        semaphore.release();
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Persist success, fire persistFinish event. version:%d", version));
        }
    }

    private void remove(String fileName) {
        File file = log.basePath.resolve(fileName).toFile();

        try {
            DateTime now = DateTime.now();

            Path yearPath = logBackupPath.resolve(String.valueOf(now.getYear()));
            File dir = yearPath.toFile();
            if (!dir.exists()) {
                boolean mkdir = dir.mkdirs();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Create base dir:%s, %b", dir.getName(), mkdir));
                }
            }
            ZipFile zipFile = new ZipFile(yearPath.resolve(String.format("%d_%d_%d.zip", now.getYear(), now.getMonthOfYear(), now.getDayOfMonth())).toFile());

            ZipParameters parameters = new ZipParameters();
            zipFile.addFile(file, parameters);
        } catch (ZipException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Backup log file error. FileName:%s", file), e);
            }
        }

        boolean success = file.delete();
        if (success) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Delete file success. file name:%s", fileName));
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Delete file fail. file name:%s", fileName));
            }
        }
    }

    public boolean isPersisting() {
        return !persistQueue.isEmpty() || semaphore.availablePermits() != SEMAPHORE_PERMITS;
    }

    public BlockingQueue<PersistTask> getPersistQueue() {
        return persistQueue;
    }

    public static void addPersistCount() {
        logPersistCount++;
    }

    public static int getLogPersistCount() {
        return logPersistCount;
    }
}
