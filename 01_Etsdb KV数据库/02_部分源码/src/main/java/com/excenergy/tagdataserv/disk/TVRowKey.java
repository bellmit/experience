package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.TagDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.excenergy.tagdataserv.Utils.timeFormat;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-23
 */
public class TVRowKey {
    private static final Logger logger = LoggerFactory.getLogger(TVRowKey.class);
    public static final int KEY_LENGTH = 12;
    private int handle;
    private long baseTime;

    public TVRowKey(int handle, long baseTime) {
        this.handle = handle;
        this.baseTime = baseTime;
    }

    public TVRowKey(byte[] key) {
        if (key == null || key.length != KEY_LENGTH) {
            String code = "E-000003";
            String msg = String.format(TagDataException.getMsg(code));
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        ByteBuffer buffer = ByteBuffer.wrap(key);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.rewind();
        handle = buffer.getInt();
        baseTime = buffer.getLong();
    }

    public int getHandle() {
        return handle;
    }

    public long getBaseTime() {
        return baseTime;
    }

    public byte[] getKey() {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(handle);
        buffer.putLong(baseTime);
        return buffer.array();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TVRowKey)) {
            return false;
        }

        TVRowKey tvRowKey = (TVRowKey) o;

        if (baseTime != tvRowKey.baseTime) {
            return false;
        }
        if (handle != tvRowKey.handle) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = handle;
        result = 31 * result + (int) (baseTime ^ (baseTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "TVRowKey{" +
                "handle=" + handle +
                ", baseTime=" + timeFormat(baseTime) +
                '}';
    }
}
