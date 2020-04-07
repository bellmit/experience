package com.excenergy.tagdataserv;

import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.net.TagValueMapper;

import java.util.Arrays;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-20
 */
public class TV {
    /**
     * 实时值生成的时间
     */
    private long timestamp;

    /**
     * 位号的值，第1个字节表示质量码
     */
    private byte[] value;

    /**
     * 创建一个实时值
     *
     * @param timestamp 该值生成的时间戳
     * @param value     值的内容，第1个表示质量码，后面是值内容
     */
    public TV(long timestamp, byte[] value) {
        this.timestamp = timestamp - (timestamp % 1000);
        this.value = value.clone();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getValue() {
        return value.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TV)) {
            return false;
        }

        TV tv = (TV) o;

        if (timestamp != tv.timestamp) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }

    @Override
    public String toString() {
        return "TV{" +
                "timestamp=" + timestamp +
                ", value=" + Arrays.toString(value) +
                "} ";
    }

    public String toString(short valType) {
        TagValue trans = TagValueMapper.trans(this, valType);
        return "TV{" +
                "timestamp=" + trans.getTime() +
                ", valType=" + valType +
                ", val=" + trans.getVal() +
                ", quality=" + trans.getQuality() +
                "} ";
    }
}
