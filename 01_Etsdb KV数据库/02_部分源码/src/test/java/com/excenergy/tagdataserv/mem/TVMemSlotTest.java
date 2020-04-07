package com.excenergy.tagdataserv.mem;

import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagdataserv.disk.TVDB;
import com.excenergy.tagmeta.RealTag;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.NavigableMap;
import java.util.TreeMap;

import static junit.framework.TestCase.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * TVMemSlot Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class TVMemSlotTest {

    private TVDB tvdb;
    private TVMemPool memPool;
    private Long onlineTime;
    private int handle;
    private DateTime dateTime = new DateTime(2013, 9, 5, 10, 42, 18, 0);
    private byte[] value = {(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};
    private TV tv = new TV(dateTime.getMillis(), value);

    private RealTag tag;
    private TagFactory tagFactory;

    private TVMemSlot slot;

    @Before
    public void before() throws Exception {
        tag = mock(RealTag.class);
        tagFactory = mock(TagFactory.class);
        when(tagFactory.get(anyInt())).thenReturn(tag);

        tvdb = mock(TVDB.class);

        memPool = mock(TVMemPool.class);

        onlineTime = new DateTime(2010, 10, 10, 10, 10, 0).getMillis();

        handle = 133;
        slot = new TVMemSlot(handle, memPool, tagFactory, tvdb);
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: put(TVMemItem item)
     */
    @Test
    public void testPut() throws Exception {
        // 1. 参数为空
        try {
            slot.put(null);// test null
        } catch (TagDataException e) {
            assertNotNull(e);
            assertTrue(e.getCode().equals("E-000008"));
        }

        // 2. 触发内存库满
        int cacheMax = 3;
        when(tag.getCacheMax()).thenReturn(cacheMax);
        when(tag.isPersist()).thenReturn(true);
        for (int i = 0; i < 5; i++) {
            slot.put(new TVMemItem(new TV(dateTime.plusMinutes(i).getMillis(), value)));
        }
        verify(memPool, times(2)).memFill();
        assertTrue(slot.getTVCount() == 5);

        // 3. 移除过期数据
        when(tag.isPersist()).thenReturn(false);
        for (int i = 5; i < 10; i++) {
            slot.put(new TVMemItem(new TV(dateTime.plusMinutes(i).getMillis(), value)));
            assertTrue(slot.getTVCount() == cacheMax);
        }
    }

    /**
     * Method: getVal()
     */
    @Test
    public void testGetVal() throws Exception {
        // 1. 内存库中未缓存，历史库中找不到
        when(tvdb.get(handle)).thenReturn(null);
        assertNull(slot.getVal());
        verify(tvdb, times(1)).get(handle);

        // 2. 内存库中未缓存，历史库中找到
        when(tvdb.get(handle)).thenReturn(tv);
        assertTrue(tv.equals(slot.getVal()));
        verify(tvdb, times(2)).get(handle);

        // 3. 内存中已经缓存，不再读历史库
        assertTrue(tv.equals(slot.getVal()));
        verify(tvdb, times(2)).get(handle);
    }

    /**
     * Method: getVal(long timestamp)
     */
    @Test
    public void testGetValTimestamp() throws Exception {
        // 1. 历史库中找不到
        when(tvdb.get(handle, tv.getTimestamp())).thenReturn(null);
        assertNull(slot.getVal(tv.getTimestamp()));
        verify(tvdb, times(1)).get(handle, tv.getTimestamp());

        // 2. 历史库中找到
        when(tvdb.get(handle, tv.getTimestamp())).thenReturn(tv);
        assertTrue(tv.equals(slot.getVal(tv.getTimestamp())));
        verify(tvdb, times(2)).get(handle, tv.getTimestamp());

        // 3. 内存库中找到
        int cacheMax = 100;
        when(tag.getCacheMax()).thenReturn(cacheMax);
        when(tag.isPersist()).thenReturn(true);
        slot.put(new TVMemItem(tv));
        assertTrue(tv.equals(slot.getVal(tv.getTimestamp())));
        verify(tvdb, times(2)).get(handle, tv.getTimestamp());
    }

    /**
     * Method: iterator(long startTime, long endTime)
     */
    @Test
    public void testIterator() throws Exception {
        // 1. 开始时间大于结束时间
        long memStart = dateTime.minusSeconds(99).getMillis();
        long memEnd = dateTime.getMillis();
        try {
            slot.iterator(memEnd, dateTime.minusSeconds(6).getMillis()); // 开始时间晚于结束时间异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000006".equals(e.getCode()));
        }

        // 2. 内存库中有全部数据
        when(tag.getCacheMax()).thenReturn(1000);
        when(tag.isPersist()).thenReturn(true);
        for (int i = 0; i < 100; i++) {
            slot.put(new TVMemItem(new TV(dateTime.minusSeconds(i).getMillis(), value)));
        }
        NavigableMap<Long, TV> map = slot.iterator(memStart, memEnd);
        assertTrue(map.size() == 100);
        verify(tvdb, times(0)).iterator(handle, memStart, memEnd);

        // 3. 全部在历史库
        long hisStart = dateTime.minusSeconds(199).getMillis();
        long hisEnd = dateTime.minusSeconds(100).getMillis();

        // 3.1 历史库返回为空
        when(tvdb.iterator(handle, hisStart, hisEnd)).thenReturn(null);
        map = slot.iterator(hisStart, hisEnd);
        assertNull(map);
        verify(tvdb, times(1)).iterator(handle, hisStart, hisEnd);

        // 3.2 历史库返回正常
        TreeMap<Long, TV> hisResult = new TreeMap<>();
        for (int i = 100; i < 200; i++) {
            TV t = new TV(dateTime.minusSeconds(i).getMillis(), value);
            hisResult.put(t.getTimestamp(), t);
        }

        when(tvdb.iterator(handle, hisStart, hisEnd)).thenReturn(hisResult);
        map = slot.iterator(hisStart, hisEnd);
        assertTrue(map.size() == 100);
        verify(tvdb, times(2)).iterator(handle, hisStart, hisEnd);

        // 4. 部分在内存库，部分在历史库
        // 4.1 历史库返回为空
        when(tvdb.iterator(handle, hisStart, hisEnd)).thenReturn(null);
        map = slot.iterator(hisStart, memEnd);
        assertTrue(map.size() == 100);
        verify(tvdb, times(3)).iterator(handle, hisStart, hisEnd);

        // 4.2 历史库返回正常
        when(tvdb.iterator(handle, hisStart, hisEnd)).thenReturn(hisResult);
        map = slot.iterator(hisStart, memEnd);
        assertTrue(map.size() == 200);
        verify(tvdb, times(4)).iterator(handle, hisStart, hisEnd);

        // 4.3 从历史库中读出来的数据，放入了内存库
        map = slot.iterator(hisStart, memEnd);
        assertTrue(map.size() == 200);
        verify(tvdb, times(4)).iterator(handle, hisStart, hisEnd); // 仍是4次，没有读历史库
    }

    /**
     * Method: getStartTime()
     */
    @Test
    public void testGetStartTime() throws Exception {
        assertTrue(slot.getStartTime() == onlineTime);

        when(tag.getCacheMax()).thenReturn(1000);
        when(tag.isPersist()).thenReturn(true);
        slot.put(new TVMemItem(tv, true));
        assertTrue(slot.getStartTime() == slot.getVal().getTimestamp());
    }
} 
