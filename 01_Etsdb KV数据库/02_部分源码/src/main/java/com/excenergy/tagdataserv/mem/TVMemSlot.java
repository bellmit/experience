package com.excenergy.tagdataserv.mem;

import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagdataserv.disk.TVDB;
import com.excenergy.tagmeta.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import static com.excenergy.tagdataserv.TagDataException.getMsg;
import static com.excenergy.tagdataserv.Utils.timeFormat;

/**
 * God Bless You! Author: Li Pengpeng Date: 2013-08-20
 */
public class TVMemSlot {
    private static final Logger logger = LoggerFactory.getLogger(TVMemSlot.class);

    private final TagFactory tagFactory;
    private final TVMemPool memPool;
    private final TVDB tvdb;
    private int memSize;

    private final int handle;

    private NavigableMap<Long, TVMemItem> valList = new TreeMap<>();
    private int size = 0;

    public TVMemSlot(int handle, TVMemPool tvMemPool, TagFactory tagFactory, TVDB tvdb) {
        this.handle = handle;
        this.memPool = tvMemPool;
        this.tagFactory = tagFactory;
        this.tvdb = tvdb;
    }

    public TVMemSlot(int handle, Application app) {
        this.handle = handle;
        this.memPool = app.getMemPool();
        this.tagFactory = app.getTagFactory();
        this.tvdb = app.getTvdb();
        memSize = app.getConfig().getInt("mem.size") * 1024 * 1024;

        try {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Get current value, the currentVal is null, read from disk. handle:%d", handle));
            }
            TV tv = tvdb.get(handle);
            if (tv == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Can't find latest value in disk. handle:%d", handle));
                }
                return;
            }
            put(new TVMemItem(tv, true));
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Read disk IO Error, handle:%d", handle), e);
            }
        }
    }

    /**
     * ???????????????????????????????????????
     *
     * @param item ????????????
     */
    public synchronized void put(TVMemItem item) {
        if (item == null) {
            String code = "E-000008";
            String msg = String.format(getMsg(code), "TVMemItem", "TVMemSlot.put");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        Tag tag = tagFactory.get(handle);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Put TVMemItem to Memory DB, Item:%s, handle:%d", item.toString(tag.getValType()), handle));
        }
        TV tv = item.getTv();

        TV real = getVal();
        if (real == null || tv.getTimestamp() > real.getTimestamp()) {
            try {
                tvdb.put(handle, tv);
            } catch (IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Read disk IO Error, handle:%d", handle), e);
                }
            }
        }

        if (!valList.containsKey(tv.getTimestamp())) {
            incrementSize();
        }
        valList.put(tv.getTimestamp(), item);

        int cacheMax = tag.getCacheMax();
        boolean persist = tag.isPersist();
        while (getSize() > cacheMax || memPool.getMemTVCount() * 135 > memSize) {
            Map.Entry<Long, TVMemItem> entry = valList.firstEntry();
            Long key = entry.getKey();
            TVMemItem value = entry.getValue();
            if (value.hasPersist() || !persist) { // ??????????????????????????? ??? ?????????????????????
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Remove TVMemItem from Memory DB, Item:%s, hanlde:%d.", value.toString(tag.getValType()), handle));
                }
                valList.remove(key);
                decrementSize();
            } else { // may be fire persist
                memPool.memFill();
                break;
            }
        }
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @return ?????????????????????????????????????????????????????????????????????????????????
     */
    public synchronized TV getVal() {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get current value, handle:%d", handle));
        }
        Map.Entry<Long, TVMemItem> last = valList.lastEntry();
        return last == null ? null : last.getValue().getTv();
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     *
     * @param timestamp ??????????????????????????????0
     * @return ??????????????????????????????????????????????????????????????????????????????????????????
     */
    public synchronized TV getVal(long timestamp) {
        timestamp -= timestamp % 1000;
        TVMemItem item = valList.get(timestamp);
        Tag tag = tagFactory.get(handle);
        if (item != null) {
            TVMemPool.addMemReadTVCount(1);
            TVMemPool.addTotalReadTVCount(1);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Found from Memory DB, handle:%d", handle));
            }
            return item.getTv();
        }
        try {
            TV tv = tvdb.get(handle, timestamp);
            if (tv == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Can't found from disk, handle:%d, timestamp:%d", handle, timestamp));
                }
                return null;
            }

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Found TV from disk, handle:%d, timestamp:%d, TV:%s", handle, timestamp, tv.toString(tag.getValType())));
            }
            TVMemPool.addTotalReadTVCount(1);
            return tv;
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Read disk IO Error, handle:%d", handle), e);
            }
            return null;
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param startTime ????????????????????????????????????????????????????????????
     * @param endTime   ????????????????????????????????????????????????????????????
     * @return ?????????????????????????????????????????????????????????????????????????????????
     */
    public synchronized NavigableMap<Long, TV> iterator(long startTime, long endTime) {
        if (startTime > endTime) {
            long tmp = startTime;
            startTime = endTime;
            endTime = tmp;
        }
        long s = getStartTime();
        Tag tag = tagFactory.get(handle);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get iterator, handle:%s, startTime:%s, endTime:%s, Mem start time:%s", handle, timeFormat(startTime), timeFormat(endTime), timeFormat(s)));
        }
        NavigableMap<Long, TV> result;
        if (startTime >= s) { // ?????????????????????????????????startTime???endTime??????
            result = readMem(startTime, endTime);
        } else if (startTime < s && endTime >= s) { // ?????????????????????????????????????????? start???endTime ??? ????????????startTime???start??????
            result = readMem(s, endTime);
            NavigableMap<Long, TV> map = readDisk(startTime, s - 1000); // ???????????????????????????????????????
            if (map != null && !map.isEmpty()) {
                result.putAll(map);
                for (Long k = map.lastKey(); getSize() < tag.getCacheMax() && k != null; k = map.lowerKey(k)) {
                    put(new TVMemItem(map.get(k), true));
                }
            }
        } else { // ???????????????????????????????????????startTime???endTime??????
            result = readDisk(startTime, endTime);
        }
        if (result != null) {
            TVMemPool.addTotalReadTVCount(result.size());
        }
        return result;
    }

    public int getTVCount() {
        return getSize();
    }

    public boolean isEmpty() {
        return getSize() < 1;
    }

    private synchronized TreeMap<Long, TV> readMem(long startTime, long endTime) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Read from memory, handle:%s, startTime:%s, endTime:%s", handle, timeFormat(startTime), timeFormat(endTime)));
        }
        NavigableMap<Long, TVMemItem> map = valList.subMap(startTime, true, endTime, true);
        TreeMap<Long, TV> result = new TreeMap<>();
        for (Map.Entry<Long, TVMemItem> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getTv());
        }
        TVMemPool.addMemReadTVCount(result.size());
        return result;
    }

    private NavigableMap<Long, TV> readDisk(long startTime, long endTime) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Read from disk, handle:%s, startTime:%s, endTime:%s", handle, timeFormat(startTime), timeFormat(endTime)));
        }
        try {
            return tvdb.iterator(handle, startTime, endTime);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Read disk IO Error, handle:%d", handle), e);
            }
            return new TreeMap<>();
        }
    }

    synchronized long getStartTime() {
        if (getSize() < 1) {
            return Long.MAX_VALUE;
        }
        return valList.firstKey();
    }

    public synchronized void clean() {
        valList.clear();
        size = 0;
    }

    private synchronized int getSize() {
        return size;
    }

    private synchronized void decrementSize() {
        size--;
    }

    private synchronized void incrementSize() {
        size++;
    }
}
