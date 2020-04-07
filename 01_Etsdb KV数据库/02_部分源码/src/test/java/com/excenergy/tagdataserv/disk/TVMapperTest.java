package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagmeta.RealTag;
import com.excenergy.tagmeta.Tag;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TVMapper Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class TVMapperTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: rowKey(int handle, long timestamp)
     */
    @Test
    public void testRowKey() throws Exception {
        int handle = 100;
        Tag tag = new RealTag();
        tag.setHandle(handle);
        tag.setMergeInterval(Tag.DAILY);
        TagFactory tagFactory = mock(TagFactory.class);
        when(tagFactory.get(handle)).thenReturn(tag);
        DateTime dateTime = new DateTime(2013, 8, 27, 8, 46, 57, 100);

        TVRowKey key = TVMapper.rowKey(handle, dateTime.getMillis(), Tag.HOURLY);
        assertTrue(key.getHandle() == handle);
        assertTrue(key.getBaseTime() == TVMapper.baseTime(dateTime.getMillis(), Tag.HOURLY));

        tag.setMergeInterval(Tag.HOURLY);

        key = TVMapper.rowKey(handle, dateTime.getMillis(), Tag.HOURLY);
        assertTrue(key.getHandle() == handle);
        assertTrue(key.getBaseTime() == TVMapper.baseTime(dateTime.getMillis(), Tag.HOURLY));
    }

    /**
     * Method: tv2Row(int handle, List<TV> tvList) and Method: row2TV(TVRowKey key, TVRowValue value)
     */
    @Test
    public void testTv2Row() throws Exception {
        int handle = 111;
        ArrayList<TV> tvs = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put((byte) 192);
        buffer.putDouble(19.8);
        DateTime dt = new DateTime(2012, 8, 26, 8, 51, 18, 0);

        TV tv1 = new TV(dt.getMillis(), buffer.array());
        TV tv2 = new TV(dt.plusHours(1).getMillis(), buffer.array());
        TV tv3 = new TV(dt.plusDays(1).getMillis(), buffer.array());
        TV tv4 = new TV(dt.plusMonths(1).getMillis(), buffer.array());
        TV tv5 = new TV(dt.plusYears(1).getMillis(), buffer.array());
        tvs.add(tv1);
        tvs.add(tv2);
        tvs.add(tv3);
        tvs.add(tv4);
        tvs.add(tv5);

        Tag tag = new RealTag();
        tag.setHandle(handle);
        tag.setMergeInterval(Tag.HOURLY);
        TagFactory tagFactory = mock(TagFactory.class);
        when(tagFactory.get(handle)).thenReturn(tag);

        Map<TVRowKey, TVRowValue> rowMap = TVMapper.tv2Row(handle, tvs, Tag.HOURLY);

        assertTrue(rowMap.size() == 5);
        TreeMap<Long, TV> longTVTreeMap = new TreeMap<>();
        for (Map.Entry<TVRowKey, TVRowValue> rowEntry : rowMap.entrySet()) {
            longTVTreeMap.putAll(TVMapper.row2TV(rowEntry.getKey(), rowEntry.getValue()));
        }

        Collection<TV> values = longTVTreeMap.values();

        assertTrue(tvs.containsAll(values));
        assertTrue(values.containsAll(tvs));

        try {
            TVMapper.tv2Row(handle, null, Tag.HOURLY); // 值不能为空异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000008".equals(e.getCode()));
        }

        try {
            TVMapper.row2TV(null, null); // 值不能为空异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000008".equals(e.getCode()));
        }
    }

    /**
     * Method: baseTime(long timestamp, char mergeInterval)
     */
    @Test
    public void testBaseTime() throws Exception {
        DateTime dateTime = new DateTime(2013, 8, 27, 9, 49, 18, 101);
        DateTime hour = new DateTime(2013, 8, 27, 9, 0, 0, 0);
        DateTime day = new DateTime(2013, 8, 27, 0, 0, 0, 0);
        DateTime month = new DateTime(2013, 8, 1, 0, 0, 0, 0);

        assertTrue(TVMapper.baseTime(dateTime.getMillis(), Tag.HOURLY) == hour.getMillis());
        assertTrue(TVMapper.baseTime(dateTime.getMillis(), Tag.DAILY) == day.getMillis());
        assertTrue(TVMapper.baseTime(dateTime.getMillis(), Tag.MONTHLY) == month.getMillis());

        try {
            TVMapper.baseTime(dateTime.getMillis(), (byte) 0x0F); //
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000005".equals(e.getCode()));
        }
    }

    /**
     * Method: toInt(byte b, byte b1, byte b2) and Method: fromInt(int i)
     */
    @Test
    public void testToInt() throws Exception {
        int t = 1000;
        byte[] bytes = TVMapper.fromInt(t);
        int i = TVMapper.toInt(bytes[0], bytes[1], bytes[2]);
        assertTrue(i == t);

        t = 0;
        TVMapper.fromInt(t);
        assertTrue(true);

        t = 0x7fffff;
        TVMapper.fromInt(t);
        assertTrue(true);

        t = 0x800000;
        try {
            TVMapper.fromInt(t); //
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000004".equals(e.getCode()));
        }

        t = -1;
        try {
            TVMapper.fromInt(t); //
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000004".equals(e.getCode()));
        }
    }

    /**
     * Method: encode(TreeMap<Integer, byte[]> vMap) and Method: decode(byte[] values)
     */
    @Test
    public void testEncodeDecode() throws Exception {
        // 长度相等
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

        byte[] encode = TVMapper.encode(para);
        Map<Integer, byte[]> result = TVMapper.decode(encode);
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

        encode = TVMapper.encode(para);
        result = TVMapper.decode(encode);
        for (Integer integer : para.keySet()) {
            assertArrayEquals(para.get(integer), result.get(integer));
        }

        try {
            TVMapper.encode(null); // 值不能为空异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000008".equals(e.getCode()));
        }

        try {
            TVMapper.decode(null); // 值不能为空异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000008".equals(e.getCode()));
        }
    }

} 
