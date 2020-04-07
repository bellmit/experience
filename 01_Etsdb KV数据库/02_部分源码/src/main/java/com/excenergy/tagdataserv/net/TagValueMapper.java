package com.excenergy.tagdataserv.net;

import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.TV;
import com.excenergy.tagmeta.Tag;
import org.joda.time.DateTime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static com.excenergy.protocol.CmdDataMapper.decodeStr;
import static com.excenergy.protocol.CmdDataMapper.encodeStr;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-16
 */
public class TagValueMapper {
    public static TV trans(TagValue tagValue) {
        DateTime time = tagValue.getTime();
        short quality = tagValue.getQuality();
        short valType = tagValue.getValType();
        Object value = tagValue.getVal();

        ByteBuffer bf = ByteBuffer.allocate(256);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        bf.put((byte) quality);
        switch (valType) {
            case TagValue.NULL:
                break;
            case TagValue.BOOL:
                bf.put((byte) (((boolean) value) ? 0x01 : 0x00));
                break;
            case TagValue.BYTE:
                bf.put((Byte) value);
                break;
            case TagValue.SHORT:
                bf.putShort((Short) value);
                break;
            case TagValue.INT:
                bf.putInt((Integer) value);
                break;
            case TagValue.LONG:
                bf.putLong((Long) value);
                break;
            case TagValue.FLOAT:
                bf.putFloat((Float) value);
                break;
            case TagValue.DOUBLE:
                bf.putDouble((Double) value);
                break;
            case TagValue.STR:
                encodeStr((String) value, bf);
                break;
            default:
                throw new UnsupportedOperationException(String.format("Unsupported Operation:%d", valType));
        }

        return new TV(time.millisOfSecond().withMinimumValue().getMillis(), Arrays.copyOf(bf.array(), bf.position()));
    }

    public static TagValue trans(TV tv, short valType) {
        DateTime time = new DateTime(tv.getTimestamp()).millisOfSecond().withMinimumValue();

        ByteBuffer bf = ByteBuffer.wrap(tv.getValue());
        bf.order(ByteOrder.LITTLE_ENDIAN);
        short quality = (short) (bf.get() & 0xFF);
        Object value;
        switch (valType) {
            case Tag.NULL:
                value = null;
                break;
            case Tag.BOOL:
                value = (bf.get() == 0x01);
                break;
            case Tag.BYTE:
                value = bf.get();
                break;
            case Tag.SHORT:
                value = bf.getShort();
                break;
            case Tag.INT:
                value = bf.getInt();
                break;
            case Tag.LONG:
                value = bf.getLong();
                break;
            case Tag.FLOAT:
                value = bf.getFloat();
                break;
            case Tag.DOUBLE:
                value = bf.getDouble();
                break;
            case Tag.STR:
                value = decodeStr(bf);
                break;
            default:
                throw new UnsupportedOperationException(String.format("Unsupported Operation:%d", valType));
        }
        return new TagValue(time, valType, value, quality);
    }
}
