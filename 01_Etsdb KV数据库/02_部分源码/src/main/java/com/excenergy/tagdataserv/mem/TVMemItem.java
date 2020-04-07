package com.excenergy.tagdataserv.mem;

import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.log.LatestPersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-20
 */
public class TVMemItem {
    private static final Logger logger = LoggerFactory.getLogger(TVMemItem.class);
    private static final ReadWriteLock lock = new ReentrantReadWriteLock(false);
    private static int maxVersion = 0;

    private TV tv;

    /**
     * 内存库全局版本号，在内存库中唯一标识一个实时值，用来判断是否已经入库
     */
    private int version;

    public TVMemItem(TV tv) {
        this(tv, false);
    }

    /**
     * 创建一个内存库的项，在内存中唯一标识一个实时值
     *
     * @param tv         实时值
     * @param hasPersist 是否已经持久化，如果是从历史库中读出来的，则为true
     */
    public TVMemItem(TV tv, boolean hasPersist) {
        if (tv == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "TV", "TVMemItem.Constructor");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        this.tv = tv;
        if (hasPersist) {
            this.version = -1;
        } else {
            this.version = nextVersion();
        }
    }

    private static int nextVersion() {
        lock.writeLock().lock();
        if (maxVersion == Integer.MAX_VALUE) {
            maxVersion = 0;
        }
        maxVersion++;
        lock.writeLock().unlock();
        return maxVersion();
    }

    private static int maxVersion() {
        lock.readLock().lock();
        int result = maxVersion;
        lock.readLock().unlock();
        return result;
    }

    public TV getTv() {
        return tv;
    }

    public int getVersion() {
        return version;
    }

    public void setHasPersist() {
        version = -1;
    }

    public boolean hasPersist() {
        // 从历史库中读出来的
        if (version == -1) {
            return true;
        }

        // 从latestPersistVersion到maxVersion之间的，是未入库的；
        // 剩下的就是已经持久化的
        int max = maxVersion();
        int latest = LatestPersist.getVersion();

        if (max >= latest) {
            return version < latest || version > max;
        } else {
            return version < latest && version > max;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TVMemItem)) {
            return false;
        }

        TVMemItem tvMemItem = (TVMemItem) o;

        if (version != tvMemItem.version) {
            return false;
        }
        if (!tv.equals(tvMemItem.tv)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = tv.hashCode();
        result = 31 * result + version;
        return result;
    }

    @Override
    public String toString() {
        return "TVMemItem{" +
                "tv=" + tv +
                ", version=" + version +
                "} ";
    }

    public String toString(short valType) {
        return "TVMemItem{" +
                "tv=" + tv.toString(valType) +
                ", version=" + version +
                "} ";
    }
}
