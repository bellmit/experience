package com.excenergy.tagdataserv.log;

import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LogMapper {
    private static final Logger logger = LoggerFactory.getLogger(LogMapper.class);

    /**
     * 将该实时值转换成字节表示
     * 格式：每条记录由5部分组成，从前到后为：开始标识(2)，Handle(4)，timestamp(8)，value.length(1)，value(value.length)；
     *
     * @param handle 位号
     * @param tv     实时值
     * @return 该位号实时值对应的字节表示
     */
    public static ByteBuffer encode(int handle, TV tv) {
        if (tv == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "TV", "LogMapper.encode");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Encode handle:%d, TV:%s", handle, tv));
        }
        ByteBuffer bf = ByteBuffer.allocate(15 + tv.getValue().length);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        bf.putShort((short) 0xffff); // 2 bytes
        bf.putInt(handle); // 4 bytes
        bf.putLong(tv.getTimestamp()); // 8 bytes
        bf.put((byte) tv.getValue().length); // 1 bytes
        bf.put(tv.getValue()); // if double 8 bytes, total 23
        bf.rewind();
        return bf;
    }

    /**
     * 把一个Block的ByteBuffer转成实时值
     *
     * @param bf 一个Block的Buffer
     * @return 这个Block中包含的实时值的Map
     */
    public static Map<Integer, Set<TV>> decode(ByteBuffer bf) {
        if (bf == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "bf", "LogMapper.encode");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        Map<Integer, Set<TV>> result = new HashMap<>();
        while (bf.remaining() > 15) { // 一条记录
            bf.mark();
            if ((bf.getShort() & 0xffff) == 0xffff) { // 开始标识
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("decode: Found a record start."));
                }
                int handle = bf.getInt();
                long timestamp = bf.getLong();
                int length = bf.get() & 0xff;
                if (bf.remaining() >= length) {
                    byte[] value = new byte[length];
                    bf.get(value);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("decode: Read a TV:%s", new TV(timestamp, value)));
                    }
                    if (result.get(handle) == null) {
                        result.put(handle, new HashSet<TV>());
                    }
                    Set<TV> tvs = result.get(handle);
                    TV tv = new TV(timestamp, value);
                    if (tvs.contains(tv)) {
                        tvs.remove(tv);
                    }
                    tvs.add(tv);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("decode: Reach the end of block, now need to read next block.");
                    }
                    bf.reset();
                    break;
                }
            } else { // 寻找下一个开始标识，如果中间的一个坏了，可以继续读后面的
                byte b = bf.get();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("decode: something unexpected occurred, finding next record start. Next byte:%d", b));
                }
            }
        }
        return result;
    }
}