package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.TagDataException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

/**
 * TVRowValue Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class TVRowValueTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: encode()
     */
    @Test
    public void testEncode() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put((byte) 192);
        buffer.putDouble(19.8);
        byte[] array = buffer.array();

        TreeMap<Integer, byte[]> para = new TreeMap<>();
        para.put(1, array);
        para.put(2, array);
        byte[] clone = array.clone();
        clone[0] = 100;
        para.put(3, clone);
        para.put(4, array);
        para.put(5, array);

        Map<Integer, byte[]> result = new TVRowValue(new TVRowValue(para).encode()).getValueMap();
        for (Integer integer : para.keySet()) {
            assertArrayEquals(para.get(integer), result.get(integer));
        }

        // 长度不等
        para.clear();

        ByteBuffer b = ByteBuffer.allocate(100);
        b.put((byte) 192);
        b.put("hello".getBytes());
        para.put(100000, Arrays.copyOf(b.array(), b.position()));
        b.clear();

        b.put((byte) 100);
        b.put("world".getBytes());
        para.put(100001, Arrays.copyOf(b.array(), b.position()));
        b.clear();

        b.put((byte) 192);
        b.put("abc".getBytes());
        para.put(100002, Arrays.copyOf(b.array(), b.position()));
        b.clear();

        b.put((byte) 100);
        b.put("How old are you?".getBytes());
        para.put(100003, Arrays.copyOf(b.array(), b.position()));
        b.clear();

        result = new TVRowValue(new TVRowValue(para).encode()).getValueMap();
        for (Integer integer : para.keySet()) {
            assertArrayEquals(para.get(integer), result.get(integer));
        }

        try {
            new TVRowValue(new byte[0]); // 值不能为空异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            Assert.assertTrue("E-000007".equals(e.getCode()));
        }
    }

    /**
     * Method: merge(TVRowValue rowValue)
     */
    @Test
    public void testMerge() throws Exception {

        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put((byte) 192);
        buffer.putDouble(19.8);
        byte[] array = buffer.array();

        TreeMap<Integer, byte[]> para = new TreeMap<>();
        para.put(1, array);
        para.put(2, array);
        byte[] clone = array.clone();
        clone[0] = 100;
        para.put(3, clone);
        para.put(4, array);
        para.put(5, array);

        TVRowValue value = new TVRowValue(para);

        // 长度不等
        para = new TreeMap<>();

        ByteBuffer b = ByteBuffer.allocate(100);
        b.put((byte) 192);
        b.put("hello".getBytes());
        para.put(100000, Arrays.copyOf(b.array(), b.position()));
        b.clear();

        b.put((byte) 100);
        b.put("world".getBytes());
        para.put(100001, Arrays.copyOf(b.array(), b.position()));
        b.clear();

        b.put((byte) 192);
        b.put("abc".getBytes());
        para.put(100002, Arrays.copyOf(b.array(), b.position()));
        b.clear();

        b.put((byte) 100);
        b.put("How old are you?".getBytes());
        para.put(100003, Arrays.copyOf(b.array(), b.position()));
        b.clear();

        TVRowValue value2 = new TVRowValue(para);

        byte[] encode = value.encode();
        TVRowValue value1 = new TVRowValue(encode);
        value.merge(value2);

        Map<Integer, byte[]> valueMap = value.getValueMap();
        Map<Integer, byte[]> value1Map = value1.getValueMap();
        Map<Integer, byte[]> value2Map = value2.getValueMap();
        for (Integer integer : value1Map.keySet()) {
            assertArrayEquals(value1Map.get(integer), valueMap.get(integer));
        }

        for (Integer integer : value2Map.keySet()) {
            assertArrayEquals(value2Map.get(integer), valueMap.get(integer));
        }

    }

} 
