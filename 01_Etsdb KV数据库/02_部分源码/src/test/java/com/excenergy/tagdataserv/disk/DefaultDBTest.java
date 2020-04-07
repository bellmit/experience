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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * DefaultDB Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class DefaultDBTest {

    private PersistDB persistDB;
    private TagFactory tagFactory;
    private DefaultDB db;

    @Before
    public void before() throws Exception {
        persistDB = mock(PersistDB.class);
        tagFactory = mock(TagFactory.class);
//        db = new DefaultDB(persistDB, tagFactory);
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: put(int handle, List<TV> tvList)
     */
    @Test
    public void testPut() throws Exception {
        int handle = 111;
        Tag tag = new RealTag();
        tag.setHandle(handle);
        tag.setMergeInterval(Tag.HOURLY);
        when(tagFactory.get(handle)).thenReturn(tag);

        Set<TV> tvs = new HashSet<>();
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put((byte) 192);
        buffer.putDouble(19.8);
        DateTime dt = new DateTime(2012, 8, 26, 8, 51, 18, 0);

        tvs.add(new TV(dt.getMillis(), buffer.array()));
        tvs.add(new TV(dt.plusHours(1).getMillis(), buffer.array()));
        tvs.add(new TV(dt.plusDays(1).getMillis(), buffer.array()));
        tvs.add(new TV(dt.plusDays(1).plusMinutes(5).getMillis(), buffer.array()));
        tvs.add(new TV(dt.plusMonths(1).getMillis(), buffer.array()));
        tvs.add(new TV(dt.plusYears(1).getMillis(), buffer.array()));

        Tag mock = mock(Tag.class);
        when(mock.getMergeInterval()).thenReturn(Tag.DAILY);
        when(tagFactory.get(handle)).thenReturn(mock);

        when(persistDB.get(any(TVRowKey.class)))//
                .thenReturn(null)//
                .thenReturn(new TVRowValue(new TreeMap<Integer, byte[]>()));

        db.put(handle, tvs);
        // 1. 能够按照时间分段，分成不同的记录
        verify(persistDB, times(4)).get(any(TVRowKey.class));
        verify(persistDB, times(4)).put(any(TVRowKey.class), any(TVRowValue.class));

        // 2. 值不能为空异常
//        try {
//            db.put(handle, null);
//            fail("Expected an TagDataException to be thrown");
//        } catch (TagDataException e) {
//            assertTrue("E-000008".equals(e.getCode()));
//        }
    }

    /**
     * Method: get(int handle)
     */
    @Test
    public void testGetHandle() throws Exception {
//        when(persistDB.getLatest(anyInt())).thenReturn(null).thenReturn(new TVRowKey(111, System.currentTimeMillis()));
//
//        // 1. 库中不存在该位号
//        db.get(110);
//        verify(persistDB, times(1)).getLatest(110);
//        verify(persistDB, times(0)).get(any(TVRowKey.class));
//
//        // 2. 数据存在异常，有Key，Value却是空的，返回null
//        when(persistDB.get(any(TVRowKey.class))).thenReturn(new TVRowValue(new TreeMap<Integer, byte[]>()));
//        assertNull(db.get(111));
//        verify(persistDB, times(1)).getLatest(111);
//        verify(persistDB, times(1)).get(any(TVRowKey.class));
//
//        // 3. 正常数据，有返回值
//        TreeMap<Integer, byte[]> vMap = new TreeMap<>();
//        vMap.put(1, new byte[]{1, 2, 3});
//        vMap.put(2, new byte[]{1, 2, 3});
//        when(persistDB.get(any(TVRowKey.class))).thenReturn(new TVRowValue(vMap));
//        assertNotNull(db.get(111));
//        verify(persistDB, times(2)).getLatest(111);
//        verify(persistDB, times(2)).get(any(TVRowKey.class));
    }

    /**
     * Method: get(int handle, long timestamp)
     */
    @Test
    public void testGetForHandleTimestamp() throws Exception {
        int handle = 111;
        Tag mock = mock(Tag.class);
        when(mock.getMergeInterval()).thenReturn(Tag.DAILY);
        when(tagFactory.get(handle)).thenReturn(mock);

        long l = System.currentTimeMillis();

        // 1. 库中不存在，返回为空
        when(persistDB.get(any(TVRowKey.class))).thenReturn(null);
        assertNull(db.get(handle, l));

        // 2. 库中存在该段时间的值，却不包含该时间点的值
        when(persistDB.get(any(TVRowKey.class))).thenReturn(new TVRowValue(new TreeMap<Integer, byte[]>()));
        assertNull(db.get(handle, l));

        // 3. 库中有值
        TreeMap<Integer, byte[]> vMap = new TreeMap<>();
        vMap.put(new DateTime(l).getSecondOfDay(), new byte[]{1, 2, 3});
        when(persistDB.get(any(TVRowKey.class))).thenReturn(new TVRowValue(vMap));
        assertNotNull(db.get(handle, l));
    }

    /**
     * Method: iterator(int handle, long startTime, long endTime)
     */
    @Test
    public void testIterator() throws Exception {
        int handle = 111;

        // 1. 开始时间晚于结束时间异常
        try {
            db.iterator(handle, 100, 99);
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000006".equals(e.getCode()));
        }

        DateTime dt = new DateTime();
        long start = dt.minusDays(3).getMillis();
        long end = dt.getMillis();

        // 2. 库中为空
        when(persistDB.get(handle, start, end)).thenReturn(null);
        assertNull(db.iterator(handle, start, end));

        when(persistDB.get(handle, start, end)).thenReturn(new HashMap<TVRowKey, TVRowValue>());
        assertNull(db.iterator(handle, start, end));

        // 3. 库中有值
        TreeMap<Integer, byte[]> vMap = new TreeMap<>();
        vMap.put(1, new byte[]{1, 2, 3});
        Map<TVRowKey, TVRowValue> map = new HashMap<>();
        map.put(TVMapper.rowKey(handle, start, Tag.DAILY), new TVRowValue(vMap));
        when(persistDB.get(handle, start, end)).thenReturn(map);
        assertNotNull(db.iterator(handle, start, end));
    }
} 
