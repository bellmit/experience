package com.excenergy.tagdataserv.mem;

import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-20
 */
public class TVMemPool {
    private static final Logger logger = LoggerFactory.getLogger(TVMemPool.class);

    private final ReadWriteLock lock = new ReentrantReadWriteLock(false);

    private static long memReadTVCount; // 内存库中找到的TV个数，用来算内存库命中率
    private static long totalReadTVCount; // 读到的总的TV个数，用来算内存库命中率

    private final ArrayList<TVMemFillListener> memFillListeners = new ArrayList<>();
    private final TagFactory tagFactory;

    private final List<TVMemSlot> memSlots = new ArrayList<>();
    private final Application app;

    private int tvCount;

    public TVMemPool(Application app) {
        this.app = app;
        this.tagFactory = app.getTagFactory();
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                int result = 0;
                for (TVMemSlot slot : memSlots) {
                    if (slot != null) {
                        result += slot.getTVCount();
                    }
                }
                lock.writeLock().lock();
                tvCount = result;
                lock.writeLock().unlock();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void put(int handle, TVMemItem v) {
        getSlot(handle).put(v);
    }

    public TV getVal(int handle) {
        return getSlot(handle).getVal();
    }

    public TV getVal(int handle, long timestamp) {
        return getSlot(handle).getVal(timestamp);
    }

    public NavigableMap<Long, TV> iterator(int handle, long startTime, long endTime) {
        return getSlot(handle).iterator(startTime, endTime);
    }

    public TVMemSlot getSlot(int handle) {
        tagFactory.checkHandle(handle);
        synchronized (memSlots) {
            while (handle >= memSlots.size()) {
                memSlots.add(null);
            }

            TVMemSlot slot = memSlots.get(handle);
            if (slot == null) {
                slot = new TVMemSlot(handle, app);
                memSlots.set(handle, slot);
            }
            return slot;
        }
    }

    public void addMemFillListener(TVMemFillListener listener) {
        if (!memFillListeners.contains(listener)) {
            memFillListeners.add(listener);
        }
    }

    public void memFill() {
        if (logger.isDebugEnabled()) {
            logger.debug("Memory is fill, now fire mem fill event.");
        }
        for (TVMemFillListener fillListener : memFillListeners) {
            fillListener.memFill();
        }
    }

    public int getMemTVCount() {
        lock.readLock().lock();
        int result = tvCount;
        lock.readLock().unlock();
        return result;
    }

    public boolean isHis(int handle, TV tv) {
        TVMemSlot slot = getSlot(handle);
        return !slot.isEmpty() && tv.getTimestamp() < slot.getStartTime();
    }

    public static synchronized void addTotalReadTVCount(int size) {
        totalReadTVCount += size;
    }

    public static synchronized void addMemReadTVCount(int size) {
        memReadTVCount += size;
    }

    public synchronized static long getMemReadTVCount() {
        return memReadTVCount;
    }

    public synchronized static long getTotalReadTVCount() {
        return totalReadTVCount;
    }

    public boolean clear(Integer handle) {
        if (handle >= memSlots.size()) {
            return true;
        }
        TVMemSlot slot = memSlots.get(handle);
        if (slot == null) {
            return true;
        }
        slot.clean();
        return true;
    }
}
