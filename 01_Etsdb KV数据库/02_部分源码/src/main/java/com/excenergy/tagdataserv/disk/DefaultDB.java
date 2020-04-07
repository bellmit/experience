package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.excenergy.tagdataserv.disk.TVMapper.*;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-10-17
 */
public class DefaultDB implements TVDB {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDB.class);

    private static long diskReadBytes; // 读磁盘字节数，用来算磁盘IO吞吐量
    private static long diskWriteBytes; // 写磁盘字节数，用来算磁盘IO吞吐量

    private PersistDB persistDB;
    private TagFactory tagFactory;

    private final BlockingQueue<Tuple<Integer, TV>> queue = new LinkedBlockingQueue<>();

    public DefaultDB(Application app, PersistDB persistDB) {
        this.persistDB = persistDB;
        this.tagFactory = app.getTagFactory();
        app.getScheduledPool().scheduleWithFixedDelay(new WriteRealTask(), 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void put(int handle, TV tv) throws IOException {
        if (tv == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "tv", "AbstractTVDB.put");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }

        if (!queue.offer(new Tuple<>(handle, tv))) {
            if (logger.isErrorEnabled()) {
                logger.error("offer real value fail.");
            }
        }
    }

    @Override
    public void put(int handle, Set<TV> tvList) throws IOException {
        if (tvList == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "tvList", "AbstractTVDB.put");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The para, handle:%d, tvList.size=%d", handle, tvList.size()));
        }

        Map<TVRowKey, TVRowValue> rowValueMap = TVMapper.tv2Row(handle, tvList, tagFactory.get(handle).getMergeInterval());
        for (Map.Entry<TVRowKey, TVRowValue> entry : rowValueMap.entrySet()) {
            TVRowKey key = entry.getKey();
            TVRowValue value = entry.getValue();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Write batch the TVRow, handle:%d, baseTime:%d", key.getHandle(), key.getBaseTime()));
            }
            TVRowValue v = persistDB.get(key);
            if (v != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("The record has exist in db, need to merge.");
                }
                v.merge(value);
                value = v;
            }
            persistDB.put(key, value);
            if (logger.isDebugEnabled()) {
                logger.debug("Put TVRow success, row key:%s", key.toString());
            }
        }
    }

    @Override
    public void put(Map<Integer, Set<TV>> tvMap) throws IOException {
        if (tvMap == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "tvMap", "AbstractTVDB.put");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The para, tvMap.size=%d", tvMap.size()));
        }

        Map<TVRowKey, TVRowValue> persistMap = new HashMap<>(200);
        for (Map.Entry<Integer, Set<TV>> entry : tvMap.entrySet()) {
            Integer handle = entry.getKey();
            Set<TV> tvList = entry.getValue();

            Map<TVRowKey, TVRowValue> rowValueMap = TVMapper.tv2Row(handle, tvList, tagFactory.get(handle).getMergeInterval());
            for (Map.Entry<TVRowKey, TVRowValue> ent : rowValueMap.entrySet()) {
                TVRowKey key = ent.getKey();
                TVRowValue value = ent.getValue();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Write batch the TVRow, handle:%d, baseTime:%d", key.getHandle(), key.getBaseTime()));
                }
                TVRowValue v = persistDB.get(key);
                if (v != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("The record has exist in db, need to merge.");
                    }
                    v.merge(value);
                    value = v;
                }
                persistMap.put(key, value);
                if (logger.isDebugEnabled()) {
                    logger.debug("Put TVRow success, row key:%s", key.toString());
                }

                if (persistMap.size() > 100) {
                    persistDB.put(persistMap);
                    persistMap.clear();
                }
            }
        }

        if (persistMap.size() > 0) {
            persistDB.put(persistMap);
        }
    }

    @Override
    public TV get(int handle) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Read the latest TV of handle:%d", handle));
        }

        byte[] value = persistDB.getReal(encodeHandle(handle));
        if (value == null) {
            return null;
        }
        return decodeTV(value);
    }

    @Override
    public TV get(int handle, long timestamp) throws IOException {
        timestamp -= timestamp % 1000;
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Read a TV with handle:%d, timestamp:%d", handle, timestamp));
        }

        TVRowKey key = TVMapper.rowKey(handle, timestamp, tagFactory.get(handle).getMergeInterval());
        TVRowValue value = persistDB.get(key);
        if (value == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The TV row is not exist, can't find TV. handle:%d, timestamp:%d", handle, timestamp));
            }
            return null;
        }
        HashMap<Long, TV> map = TVMapper.row2TV(key, value);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Found the TV row and %s TV in it. handle:%d, timestamp:%d", map.size(), handle, timestamp));
        }

        TV tv = map.get(timestamp);
        if (tv == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Can't find the TV with handle:%d, timestamp:%d", handle, timestamp));
            }
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get the TV by handle and timestamp success. handle:%d, timestamp:%d, TV:%s", handle, timestamp, tv.toString()));
        }
        return tv;
    }

    @Override
    public NavigableMap<Long, TV> iterator(int handle, long startTime, long endTime) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Create a level db iterator with handle:%d, startTime:%d, endTime:%d.", handle, startTime, endTime));
        }

        if (startTime > endTime) {
            long tmp = startTime;
            startTime = endTime;
            endTime = tmp;
        }

        Map<TVRowKey, TVRowValue> rowMap = persistDB.get(handle, startTime, endTime);
        if (rowMap == null || rowMap.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("The result is null or nothing found in db.");
            }
            return new TreeMap<>();
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Found %d TVRows with handle:%d, startTime:%d, endTime:%d.", rowMap.size(), handle, startTime, endTime));
        }
        HashMap<Long, TV> all = new HashMap<>(rowMap.size() * 100);
        for (Map.Entry<TVRowKey, TVRowValue> entry : rowMap.entrySet()) {
            HashMap<Long, TV> map = TVMapper.row2TV(entry.getKey(), entry.getValue());
            all.putAll(map);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Found %d TV rows with handle:%d, startTime:%d, endTime:%d.", all.size(), handle, startTime, endTime));
        }

        TreeMap<Long, TV> treeMap = new TreeMap<>(all);
        // remove the TV which not in the time range.
        NavigableMap<Long, TV> result = treeMap.subMap(startTime, true, endTime, true);

        if (logger.isDebugEnabled()) {
            if (result.isEmpty()) {
                logger.debug(String.format("Result is empty"));
            } else {
                logger.debug(String.format("%d TV in this iterator, the timestamp of TV from %d to %d", result.size(), result.firstKey(), result.lastKey()));
            }
        }
        return result;
    }

    @Override
    public boolean delete(int handle, long startTime, long endTime) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Create a level db iterator with handle:%d, startTime:%d, endTime:%d.", handle, startTime, endTime));
        }

        if (startTime > endTime) {
            long tmp = startTime;
            startTime = endTime;
            endTime = tmp;
        }

        return persistDB.delete(handle, startTime, endTime);
    }

    public static synchronized void addDiskReadBytes(int i) {
        diskReadBytes += i;
    }

    public static synchronized void addDiskWriteBytes(int i) {
        diskWriteBytes += i;
    }

    public static synchronized long getDiskReadBytes() {
        return diskReadBytes;
    }

    public static synchronized long getDiskWriteBytes() {
        return diskWriteBytes;
    }

    private static class Tuple<K, V> {
        private K key;
        private V val;

        private Tuple(K key, V val) {
            this.key = key;
            this.val = val;
        }
    }

    private class WriteRealTask implements Runnable {
        private ArrayList<Tuple<Integer, TV>> list = new ArrayList<>();

        @Override
        public void run() {
            while (!queue.isEmpty()) {
                try {
                    queue.drainTo(list);
                    Map<byte[], byte[]> map = new HashMap<>();
                    for (Tuple<Integer, TV> tuple : list) {
                        map.put(encodeHandle(tuple.key), encodeTV(tuple.val));
                    }
                    persistDB.putReal(map);
                } catch (Throwable e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Write real value error.", e);
                    }
                } finally {
                    list.clear();
                }
            }
        }
    }
}
