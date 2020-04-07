package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagmeta.Tag;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.excenergy.tagdataserv.TagDataException.getMsg;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-23
 */
public class TVMapper {
    private static final Logger logger = LoggerFactory.getLogger(TVMapper.class);

    public static TVRowKey rowKey(int handle, long timestamp, byte mergeInterval) {
        return new TVRowKey(handle, baseTime(timestamp, mergeInterval));
    }

    public static Map<TVRowKey, TVRowValue> tv2Row(int handle, Collection<TV> tvList, byte mergeInterval) {
        if (tvList == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "tvList", "TVMapper.tv2Row");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Transform TV to TVRow, handle:%d, TV count:%d", handle, tvList.size()));
        }

        Map<TVRowKey, HashMap<Integer, byte[]>> map = new HashMap<>();
        for (TV tv : tvList) {
            long baseTime = baseTime(tv.getTimestamp(), mergeInterval);
            TVRowKey key = rowKey(handle, baseTime, mergeInterval);
            if (!map.containsKey(key)) {
                map.put(key, new HashMap<Integer, byte[]>((int) (tvList.size() / 0.75f) + 1));
            }
            map.get(key).put(Long.valueOf((tv.getTimestamp() - baseTime) / 1000).intValue(), tv.getValue());
        }

        HashMap<TVRowKey, TVRowValue> result = new HashMap<>();
        for (Map.Entry<TVRowKey, HashMap<Integer, byte[]>> entry : map.entrySet()) {
            result.put(entry.getKey(), new TVRowValue(entry.getValue()));
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("row2TV success, the TVRow count is %d.", result.size()));
        }
        return result;
    }

    public static HashMap<Long, TV> row2TV(TVRowKey key, TVRowValue value) {
        if (key == null || value == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "key or value", "TVMapper.tv2Row");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Transform TVRow to TV, handle:%d, TV count:%d", key.getHandle(), value.getValueMap().size()));
        }
        long baseTime = key.getBaseTime();
        Map<Integer, byte[]> vMap = value.getValueMap();
        HashMap<Long, TV> result = new HashMap<>((int) (vMap.size() / 0.75f) + 1);
        for (Map.Entry<Integer, byte[]> entry : vMap.entrySet()) {
            long t = baseTime + entry.getKey() * 1000L; // 1000要是long型的，否则会越界，出现负数
            result.put(t, new TV(t, entry.getValue()));
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("row2TV success, the TV count is %d.", result.size()));
        }
        return result;
    }

    /**
     * 编码分两种模式：值长度相等 和 不等
     * Value第1个字节表示该记录中的长度是否相等
     * 如果相等，第2个字节表示每一个字节的长度，第三个字节直到结束，表示一个个的值单元
     * 如果不相等，第二个字节直到结束，表示长度 和 值单元；
     * 值单元分为3部分，时间余数，质量码，和值
     * 时间余数占用3字节，质量码0或1字节，剩下的全是值
     * 如果质量码是192，不用存储，如果不是192，时间余数第1个bit标识为1，第4个字节表示质量码
     * <p/>
     * 异常情况：
     * 时间余数最大为2的23次方，即8388608秒，长达97天，若大于range，抛异常；
     * 读值时，如果和正常预期的结束不一致，抛异常
     *
     * @param vMap key: time remaining, value: quality and value
     * @return the format of store
     */
    public static byte[] encode(Map<Integer, byte[]> vMap) {
        if (vMap == null || vMap.isEmpty()) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "vMap", "TVMapper.encode");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Encode TVRowValue, the count of TV is %d.", vMap.size()));
        }
        boolean valueLengthEquals = true;
        int vLength = vMap.values().iterator().next().length;
        int totalLength = 0;
        for (byte[] val : vMap.values()) {
            totalLength += val.length;
            if (vLength != val.length) {
                valueLengthEquals = false;
            }
        }

        if (valueLengthEquals) {// 值长度相等
            vLength = vLength - 1;// 每个值真实的长度，要减去1个字节（这个字节表示质量码）
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The value length equals, length:%d,total length:%d.", vLength, totalLength));
            }

            // 估算最大使用量，包括值长度是否相等（1字节），值长度（1字节），时间余数（每个3字节）和 所有值的长度
            ByteBuffer buffer = ByteBuffer.allocate(2 + 3 * vMap.size() + totalLength);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.put((byte) 1);// 值长度相等
            buffer.put((byte) vLength);// 值长度最长为255，超出则截断（255已经足够长了）

            // 一个值由的时间余数（3字节），0或1个质量码（1字节），值这3部分组成
            for (Map.Entry<Integer, byte[]> entry : vMap.entrySet()) {
                byte[] timeRemaining = fromInt(entry.getKey());
                byte[] v = entry.getValue();

                if ((v[0] & 0xFF) == 192) {// 质量码为192
                    buffer.put(timeRemaining); // 时间余数
                    buffer.put(Arrays.copyOfRange(v, 1, vLength + 1)); // 不保存质量码
                } else { // 质量码不是192
                    timeRemaining[0] = (byte) (timeRemaining[0] | 0x80); // 时间余数前面加上标识，第1个bit标为1
                    buffer.put(timeRemaining);// 写入标记过的时间余数
                    buffer.put(v);// 存储质量码 和 值
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Decode TVRowValue, the value is %d bytes.", buffer.position()));
            }
            return Arrays.copyOf(buffer.array(), buffer.position());
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The value length not equal.,total length:%d.", totalLength));
            }
            // 估算最大使用量，包括值长度是否相等（1字节），时间余数（每个3字节），值长度（每个1字节）和 所有值的长度
            ByteBuffer buffer = ByteBuffer.allocate(1 + (3 + 1) * vMap.size() + totalLength);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.put((byte) 0);// 值长度不等
            // 最大为 3 + 1 + 255
            for (Map.Entry<Integer, byte[]> entry : vMap.entrySet()) {
                byte[] timeRemaining = fromInt(entry.getKey());
                byte[] v = entry.getValue();

                if ((v[0] & 0xFF) == 192) {// 质量码为192
                    buffer.put(timeRemaining); // 时间余数
                    buffer.put((byte) (v.length - 1)); // 值长度是变长的，为每一个值保存长度，需要存储质量码
                    buffer.put(Arrays.copyOfRange(v, 1, v.length)); // 不保存质量码
                } else { // 质量码不是192
                    timeRemaining[0] = (byte) (timeRemaining[0] | 0x80); // 时间余数前面加上标识，第1个bit标为1
                    buffer.put(timeRemaining);// 写入标记过的时间余数
                    buffer.put(v[0]);// 存储质量码
                    buffer.put((byte) (v.length - 1));// 值的长度
                    buffer.put(Arrays.copyOfRange(v, 1, v.length)); // 不保存质量码
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Encode TVRowValue success, the value is %d bytes.", buffer.position()));
            }
            return Arrays.copyOf(buffer.array(), buffer.position());
        }
    }

    public static Map<Integer, byte[]> decode(byte[] values) {
        if (values == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "values", "TVMapper.decode");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Decode TVRowValue, the value is %d bytes.", values.length));
        }
        Map<Integer, byte[]> result = new HashMap<>((values.length / 3) + 1);

        ByteBuffer buffer = ByteBuffer.wrap(values);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();

        if (1 == buffer.get()) {// 值长度相等
            int length = (buffer.get() & 0xFF);// 有可能超过128，所以要转成int
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The value length equals, length:%d", length));
            }
            while (buffer.hasRemaining()) {
                byte[] b = new byte[length + 1];// 用来存储 质量码和值
                byte b1 = buffer.get();// 第1个字节，看看是否对质量码压缩过
                if (0x00 == (b1 & 0x80)) { // 第1个bit是0，质量码没有存储
                    int time = toInt(b1, buffer.get(), buffer.get());

                    b[0] = (byte) 192;// 没有存储，质量码为192

                    buffer.get(b, 1, length);

                    result.put(time, b);
                } else {// 第1个bit不是0，质量码有存储
                    b1 = (byte) (b1 & 0x7F);// 把第1个bit去掉
                    int time = toInt(b1, buffer.get(), buffer.get()); // 得到时间余数

                    buffer.get(b);// 读质量码和值
                    result.put(time, b);
                }
            }
        } else {// 值长度不等
            if (logger.isDebugEnabled()) {
                logger.debug("The value length not equal.");
            }
            while (buffer.hasRemaining()) {
                byte b1 = buffer.get();// 第1个字节，看看是否对质量码压缩过
                if (0x00 == (b1 & 0x80)) { // 第1个bit是0，质量码没有存储
                    int time = toInt(b1, buffer.get(), buffer.get());

                    int length = buffer.get() & 0xFF;// 有可能超过128，所以要转成 int

                    byte[] b = new byte[length + 1];
                    b[0] = (byte) 192;
                    buffer.get(b, 1, length);

                    result.put(time, b);
                } else {// 第1个bit不是0，质量码有存储
                    b1 = (byte) (b1 & 0x7F);// 把第1个bit去掉
                    int time = toInt(b1, buffer.get(), buffer.get()); // 得到时间余数

                    byte quality = buffer.get(); // 质量码

                    int length = buffer.get() & 0xFF;// 有可能超过128，所以要转成 int
                    byte[] b = new byte[length + 1];
                    b[0] = quality;
                    buffer.get(b, 1, length);

                    result.put(time, b);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Decode success, the TV count is %d.", result.size()));
        }
        return result;
    }

    public static byte[] merge(byte[] older, byte[] newer) {
        if (older[0] != newer[0]) {
            return null;
        }

        if (older[0] == 1) { //  值长度相等
            int size = older.length + newer.length - 2;
            byte[] result = new byte[size];
            System.arraycopy(older, 0, result, 0, older.length);
            System.arraycopy(newer, 2, result, older.length, newer.length - 2);
            return result;
        } else {
            // 值长度不等
            int size = older.length + newer.length - 1;
            byte[] result = new byte[size];
            System.arraycopy(older, 0, result, 0, older.length);
            System.arraycopy(newer, 1, result, older.length, newer.length - 1);
            return result;
        }
    }

    public static long baseTime(long timestamp, byte mergeInterval) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Generate baseTime, timestamp:%d, mergeInterval:%d", timestamp, mergeInterval));
        }
        DateTime dateTime = new DateTime(timestamp);
        switch (mergeInterval) {
            case Tag.HOURLY:
                return timestamp - (timestamp % (1000 * 60 * 60));
            case Tag.DAILY:
                return dateTime.millisOfDay().withMinimumValue().getMillis();
            case Tag.MONTHLY:
                return dateTime.millisOfDay().withMinimumValue().dayOfMonth().withMinimumValue().getMillis();
            default:
                String code = "E-000005";
                String msg = String.format(getMsg(code), mergeInterval);
                if (logger.isErrorEnabled()) {
                    logger.error(msg);
                }
                throw new TagDataException(code, msg);
        }
    }

    public static int toInt(byte b, byte b1, byte b2) {
        return ((0xFF & b) << 16) + ((0xFF & b1) << 8) + (0xFF & b2);
    }

    public static byte[] fromInt(int i) {
        if (i < 0 || i > 0x7fffff) {
            String code = "E-000004";
            String msg = String.format(getMsg(code), i);
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        byte[] bytes = new byte[3];
        bytes[0] = (byte) (i >> 16);
        bytes[1] = (byte) (i >> 8);
        bytes[2] = (byte) i;
        return bytes;
    }

    public static byte[] encodeHandle(int handle) {
        ByteBuffer bf = ByteBuffer.allocate(4);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        bf.putInt(handle);
        return bf.array();
    }

    public static int decodeHandle(byte[] bytes) {
        ByteBuffer bf = ByteBuffer.wrap(bytes);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        return bf.getInt();
    }

    public static byte[] encodeTV(TV tv) {
        ByteBuffer bf = ByteBuffer.allocate(9 + tv.getValue().length);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        bf.putLong(tv.getTimestamp()); // 8 bytes
        bf.put((byte) tv.getValue().length); // 1 bytes
        bf.put(tv.getValue()); // if double 8 bytes, total 23
        return bf.array();
    }

    public static TV decodeTV(byte[] bytes) {
        ByteBuffer bf = ByteBuffer.wrap(bytes);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        long timestamp = bf.getLong();
        int length = bf.get() & 0xFF;
        byte[] value = new byte[length];
        bf.get(value);
        return new TV(timestamp, value);
    }
}
