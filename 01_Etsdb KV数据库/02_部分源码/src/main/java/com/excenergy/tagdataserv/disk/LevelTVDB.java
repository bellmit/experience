package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagmeta.Tag;
import com.typesafe.config.Config;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-23
 */
public class LevelTVDB implements PersistDB {
    private static final Logger logger = LoggerFactory.getLogger(LevelTVDB.class);
    private LevelDBPara levelDBPara;
    private final TagFactory tagFactory;
    protected StoreStrategy strategy;
    protected Path path;

    private final Map<Path, Connection> connPool = Collections.synchronizedMap(new HashMap<Path, Connection>());
    private org.iq80.leveldb.Logger levelDBLogger;
    private DB realDb;

    public LevelTVDB(Application app) {
        Config config = app.getConfig();
        tagFactory = app.getTagFactory();
        strategy = TVDBFactory.createStrategy(config.getString("disk.storeStrategy"), app.getOnlineTime());
        path = Paths.get(config.getString("application.data_archive_path"));

        levelDbPara(config);

        // start close connection thread
        int keepConnectionTime = config.getInt("disk.level.keep_connection_time") * 1000;
        app.getScheduledPool().scheduleAtFixedRate(new CloseTask(keepConnectionTime), 5, 15, TimeUnit.SECONDS);

        // init level db logger
        levelDBLogger = new org.iq80.leveldb.Logger() {
            @Override
            public void log(String s) {
                if (logger.isDebugEnabled()) {
                    logger.debug(s);
                }
            }
        };

        // open real db
        try {
            realDb = openDb(path.resolve("real"));
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Open real db fail.", e);
            }
        }
    }

    private void levelDbPara(Config config) {
        levelDBPara = new LevelDBPara();
        if (config.getBoolean("disk.level.compression")) {
            levelDBPara.setCompressionType(CompressionType.SNAPPY);
        } else {
            levelDBPara.setCompressionType(CompressionType.NONE);
        }
        levelDBPara.setBlockRestartInterval(config.getInt("disk.level.block_restart_interval"));
        levelDBPara.setWriteBufferSize(config.getInt("disk.level.write_buffer_size"));
        levelDBPara.setBlockSize(config.getInt("disk.level.block_size"));
        levelDBPara.setCacheSize(config.getInt("disk.level.cache_size"));
        levelDBPara.setCreateIfMissing(config.getBoolean("disk.level.create_if_missing"));
        levelDBPara.setErrorIfExists(config.getBoolean("disk.level.error_if_exists"));
        levelDBPara.setMaxOpenFiles(config.getInt("disk.level.max_open_files"));
        levelDBPara.setParanoidChecks(config.getBoolean("disk.level.paranoid_checks"));
        levelDBPara.setVerifyChecksums(config.getBoolean("disk.level.verify_checksums"));
    }

    @Override
    public void putReal(byte[] key, byte[] value) throws IOException {
        realDb.put(key, value);
    }

    @Override
    public void putReal(Map<byte[], byte[]> map) throws IOException {
        try (WriteBatch batch = realDb.createWriteBatch()) {
            for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
                batch.put(entry.getKey(), entry.getValue());
            }
            realDb.write(batch);
        }
    }

    @Override
    public byte[] getReal(byte[] key) throws IOException {
        return realDb.get(key);
    }

    @Override
    public void put(TVRowKey key, TVRowValue value) throws IOException {
        Path path = getPath(key);
        DB db = getLevelDB(path);
        byte[] k = key.getKey();
        byte[] v = value.encode();
        DefaultDB.addDiskWriteBytes(k.length + v.length);
        db.put(k, v);
    }

    @Override
    public void put(Map<TVRowKey, TVRowValue> map) throws IOException {
        Map<Path, Map<TVRowKey, TVRowValue>> dbMap = new HashMap<>();
        for (Map.Entry<TVRowKey, TVRowValue> entry : map.entrySet()) {
            TVRowKey key = entry.getKey();
            Path path = getPath(key);
            Map<TVRowKey, TVRowValue> valueMap = dbMap.get(path);
            if (valueMap == null) {
                valueMap = new HashMap<>(map.size());
                dbMap.put(path, valueMap);
            }
            valueMap.put(key, entry.getValue());
        }

        for (Map.Entry<Path, Map<TVRowKey, TVRowValue>> entry : dbMap.entrySet()) {
            DB db = getLevelDB(entry.getKey());
            try (WriteBatch batch = db.createWriteBatch()) {
                for (Map.Entry<TVRowKey, TVRowValue> ent : entry.getValue().entrySet()) {
                    byte[] key = ent.getKey().getKey();
                    byte[] val = ent.getValue().encode();
                    batch.put(key, val);
                }
                db.write(batch);
            }
        }
    }

    @Override
    public TVRowValue get(TVRowKey key) throws IOException {
        Path path = getPath(key);
        DB db = getLevelDB(path);

        byte[] data = db.get(key.getKey());
        if (data == null) {
            return null;
        }
        DefaultDB.addDiskReadBytes(data.length);
        return new TVRowValue(data);
    }

    @Override
    public Map<TVRowKey, TVRowValue> get(int handle, long startTime, long endTime) throws IOException {
        TVRowKey startKey = TVMapper.rowKey(handle, startTime, tagFactory.get(handle).getMergeInterval());
        TVRowKey endKey = TVMapper.rowKey(handle, endTime, tagFactory.get(handle).getMergeInterval());
        Path startPath = getPath(startKey);
        Path endPath = getPath(endKey);

        List<Path> paths = new ArrayList<>();
        paths.add(startPath);
        Path next = startPath;
        while (!next.equals(endPath)) {
            next = strategy.next(next);
            if (next == null) {
                break;
            }
            paths.add(next);
        }

        TVRowKey rowKey = TVMapper.rowKey(handle, startTime, tagFactory.get(handle).getMergeInterval());
        Map<TVRowKey, TVRowValue> result = new HashMap<>();
        for (Path path : paths) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Reading DB of path:%s", path.toString()));
            }
            DB db = getLevelDB(path);

            try (DBIterator iterator = db.iterator()) {
                iterator.seek(rowKey.getKey());
                while (iterator.hasNext()) {
                    Map.Entry<byte[], byte[]> entry = iterator.next();
                    DefaultDB.addDiskReadBytes(entry.getKey().length + entry.getValue().length);

                    TVRowKey k = new TVRowKey(entry.getKey());
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Iterator key:%s", k.toString()));
                    }

                    if (k.getHandle() == handle && k.getBaseTime() <= endTime) {
                        TVRowValue v = new TVRowValue(entry.getValue());
                        result.put(k, v);
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Reach end for handle:%d, baseTime:%d.", k.getHandle(), k.getBaseTime()));
                        }
                        break;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public boolean delete(int handle, long startTime, long endTime) throws IOException {
        TVRowKey startKey = TVMapper.rowKey(handle, startTime, tagFactory.get(handle).getMergeInterval());
        TVRowKey endKey = TVMapper.rowKey(handle, endTime, tagFactory.get(handle).getMergeInterval());
        Path startPath = getPath(startKey);
        Path endPath = getPath(endKey);

        List<Path> paths = new ArrayList<>();
        paths.add(startPath);
        Path next = startPath;
        while (!next.equals(endPath)) {
            next = strategy.next(next);
            if (next == null) {
                break;
            }
            paths.add(next);
        }

        for (Path path : paths) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Reading DB of path:%s", path.toString()));
            }
            DB db = getLevelDB(path);

            try (DBIterator iterator = db.iterator()) {
                iterator.seek(startKey.getKey());
                while (iterator.hasNext()) {
                    Map.Entry<byte[], byte[]> entry = iterator.next();
                    DefaultDB.addDiskReadBytes(entry.getKey().length + entry.getValue().length);

                    TVRowKey k = new TVRowKey(entry.getKey());
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Iterator key:%s", k.toString()));
                    }

                    if (k.getHandle() == handle && k.getBaseTime() < endTime) {
                        db.delete(entry.getKey());
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Reach end for handle:%d, baseTime:%d.", k.getHandle(), k.getBaseTime()));
                        }
                        break;
                    }
                }
            }
        }

        return true;
    }

    private Path getPath(TVRowKey key) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get DB path by key:%s", key));
        }
        return strategy.path(getBasePath(key.getHandle()), key.getBaseTime());
    }

    private Path getBasePath(int handle) {
        Tag tag = tagFactory.get(handle);
        if (tag.isReal()) {
            return path.resolve("r");
        } else if (tag.isVirtual()) {
            return path.resolve("v");
        } else if (tag.isPreReal()) {
            return path.resolve("pr");
        } else if (tag.isPdr()) {
            return path.resolve("pdr");
        } else if (tag.isPhr()) {
            return path.resolve("phr");
        } else {
            String code = "E-000009";
            String msg = String.format(TagDataException.getMsg(code), tag.toString());
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
    }

    private DB getLevelDB(Path path) throws IOException {
        synchronized (connPool) {
            Connection connection = connPool.get(path);
            if (connection == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Create level db. Path:%s", path));
                }
                connection = new Connection(path, openDb(path));
                connPool.put(path, connection);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("The LevelDB is created. Path is:%s", path));
                }
            }

            return connection.getDb();
        }
    }

    private synchronized DB openDb(Path path) throws IOException {
        DB db;
        Options options = new Options();
        options.logger(levelDBLogger);
        options.compressionType(levelDBPara.getCompressionType());
        options.blockRestartInterval(levelDBPara.getBlockRestartInterval());
        options.writeBufferSize(levelDBPara.getWriteBufferSize());
        options.blockSize(levelDBPara.getBlockSize());
        options.cacheSize(levelDBPara.getCacheSize());
        options.createIfMissing(levelDBPara.isCreateIfMissing());
        options.errorIfExists(levelDBPara.isErrorIfExists());
        options.maxOpenFiles(levelDBPara.getMaxOpenFiles());
        options.paranoidChecks(levelDBPara.isParanoidChecks());
        options.verifyChecksums(levelDBPara.isVerifyChecksums());

        try {
            // create dir if not exist. add for linux
            File dir = path.toFile();
            if (!dir.isDirectory()) {
                if (dir.mkdirs()) {
                    if (logger.isInfoEnabled()) {
                        logger.info(String.format("Create DB %s success", dir.getCanonicalPath()));
                    }
                } else {
                    String code = "E-000012";
                    String msg = String.format(TagDataException.getMsg(code), dir.getCanonicalPath());
                    if (logger.isErrorEnabled()) {
                        logger.error(msg);
                    }
                    throw new TagDataException(code, msg);
                }
            }
            db = JniDBFactory.factory.open(dir, options);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Open DB %s success", dir.getAbsolutePath()));
            }
        } catch (IOException e) {
            String msg = String.format("Open level db fail. Path is:%s", path);
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new IOException(msg, e);
        }
        return db;
    }

    private static class Connection {
        private final Path path;
        private long createTime;
        private long latestUseTime;
        private DB db;

        private Connection(Path path, DB db) {
            this.path = path;
            this.db = db;
            this.createTime = System.currentTimeMillis();
            this.latestUseTime = createTime;
        }

        private long getLatestUseTime() {
            return latestUseTime;
        }

        private DB getDb() {
            this.latestUseTime = System.currentTimeMillis();
            return db;
        }

        @Override
        public String toString() {
            return "Connection{" +
                    "path=" + path +
                    ", createTime=" + createTime +
                    ", latestUseTime=" + latestUseTime +
                    '}';
        }
    }

    private class CloseTask implements Runnable {
        private int keepConnectionTime;

        public CloseTask(int keepConnectionTime) {
            this.keepConnectionTime = keepConnectionTime;
        }

        @Override
        public void run() {
            Thread.currentThread().setName("Close-Unused-DB-Connection-Thread");
            try {
                long cur = System.currentTimeMillis();
                synchronized (connPool) {
                    Iterator<Map.Entry<Path, Connection>> iter = connPool.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<Path, Connection> entry = iter.next();
                        //                for (Map.Entry<Path, Connection> entry : connPool.entrySet()) {
                        Connection conn = entry.getValue();
                        if (cur - conn.getLatestUseTime() > keepConnectionTime) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(String.format("Closing connection:%s", conn));
                            }
                            DB db = conn.getDb();
                            try {
                                db.close();
                                iter.remove();
                                if (logger.isInfoEnabled()) {
                                    logger.info(String.format("Close DB success:%s", conn));
                                }
                            } catch (IOException e) {
                                if (logger.isErrorEnabled()) {
                                    logger.error("Close level db fail.", e);
                                }
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Something unexpected occurred.", e);
                }
            }
        }
    }
}
