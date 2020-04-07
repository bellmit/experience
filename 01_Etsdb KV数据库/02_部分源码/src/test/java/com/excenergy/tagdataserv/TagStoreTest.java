package com.excenergy.tagdataserv;

import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.disk.TVDB;
import com.excenergy.tagdataserv.log.TVLog;
import com.excenergy.tagdataserv.mem.TVMemItem;
import com.excenergy.tagdataserv.mem.TVMemPool;
import com.excenergy.tagmeta.RealTag;
import com.excenergy.tagmeta.Tag;
import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * TagStore Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class TagStoreTest {

    private final TagFactory tagFactory;
    private final TVMemPool memPool;
    private final TVDB tvdb;
    private final TVLog tvLog;
    private final RealTag tag;
    private final Integer handle = 111;
    private final Integer tolerance = 600;
    private TagStore tagStore;
    private DateTime dateTime = new DateTime(2013, 10, 10, 10, 10, 10, 0);
    private byte[] value = new byte[]{(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};

    public TagStoreTest() throws InterruptedException {
        tag = mock(RealTag.class);
        when(tag.getHandle()).thenReturn(handle);
        when(tag.getCacheMax()).thenReturn(100);
        when(tag.getMergeInterval()).thenReturn(Tag.HOURLY);
        when(tag.isPersist()).thenReturn(true);
        when(tag.getTolerance()).thenReturn(tolerance);

        tagFactory = mock(TagFactory.class);
        when(tagFactory.get(anyInt())).thenReturn(tag);

        memPool = mock(TVMemPool.class);

        tvdb = mock(TVDB.class);
        tvLog = mock(TVLog.class);

        tagStore = new TagStore(handle, tagFactory, memPool, tvdb, tvLog);
    }

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: writeVal(TV tv)
     */
    @Test
    public void testWriteVal() throws Exception {
        // 1. tv为空
        try {
            TV tv = null;
//            tagStore.writeVal(tv);// test null
        } catch (TagDataException e) {
            assertNotNull(e);
            assertTrue(e.getCode().equals("E-000008"));
        }

        // 2. 有损压缩
        when(tag.isEnableCompress()).thenReturn(true);
        when(tag.getCompressAcc()).thenReturn(60);

        // 2.1 该数据的时间段内尚无数据，写入
        when(memPool.iterator(eq(handle), anyLong(), anyLong())).thenReturn(null);
//        tagStore.writeVal(new TV(dateTime.plusSeconds(1).getMillis(), value));
        verify(memPool, times(1)).iterator(eq(handle), anyLong(), anyLong());
        verify(memPool, times(1)).isHis(eq(handle), any(TV.class));
        verify(memPool, times(1)).put(eq(handle), any(TVMemItem.class)); // 写入内存库
        verify(tvLog, times(1)).append(eq(handle), any(TVMemItem.class)); // 写日志

        // 2.2 该数据的时间段内有数据，忽略该条
        TreeMap treeMap = mock(TreeMap.class);
        when(treeMap.size()).thenReturn(1);
        when(memPool.iterator(eq(handle), anyLong(), anyLong())).thenReturn(treeMap);
//        tagStore.writeVal(new TV(dateTime.plusSeconds(5).getMillis(), value));
        verify(memPool, times(2)).iterator(eq(handle), anyLong(), anyLong());
        verify(memPool, times(1)).isHis(eq(handle), any(TV.class)); // 这一次没有调用
        verify(memPool, times(1)).put(eq(handle), any(TVMemItem.class)); // 也没有写入内存库
        verify(tvLog, times(1)).append(eq(handle), any(TVMemItem.class)); // 没有写日志

        when(tag.isEnableCompress()).thenReturn(false);// 改回不启用压缩

        // 3. 补传数据
        when(memPool.isHis(eq(handle), any(TV.class))).thenReturn(true); // 设为补传数据

        // 3.1 正常持久化位号
//        tagStore.writeVal(new TV(dateTime.getMillis(), value));
        verify(tvdb, times(1)).put(eq(handle), any(Set.class)); // 写历史库
        verify(memPool, times(1)).put(eq(handle), any(TVMemItem.class)); // 没有写入内存库
        verify(tvLog, times(1)).append(eq(handle), any(TVMemItem.class)); // 没有写日志

        // 3.2 非持久化位号
        when(tag.isPersist()).thenReturn(false);

//        tagStore.writeVal(new TV(dateTime.getMillis(), value));
        verify(tvdb, times(1)).put(eq(handle), any(Set.class)); // 没有调用写历史库
        verify(memPool, times(1)).put(eq(handle), any(TVMemItem.class)); // 没有写入内存库
        verify(tvLog, times(1)).append(eq(handle), any(TVMemItem.class)); // 没有写日志

        when(tag.isPersist()).thenReturn(true);

        when(memPool.isHis(eq(handle), any(TV.class))).thenReturn(false); // 改回来

        // 4. 正常写内存库

        // 4.1 正常持久化位号，写日志缓存
//        tagStore.writeVal(new TV(dateTime.getMillis(), value));
        verify(tvdb, times(1)).put(eq(handle), any(Set.class)); // 没有写历史库
        verify(memPool, times(2)).put(eq(handle), any(TVMemItem.class)); // 写入内存库
        verify(tvLog, times(2)).append(eq(handle), any(TVMemItem.class)); // 写日志

        // 4.2 非持久化位号，不需要写日志缓存
        when(tag.isPersist()).thenReturn(false);

//        tagStore.writeVal(new TV(dateTime.getMillis(), value));
        verify(tvdb, times(1)).put(eq(handle), any(Set.class)); // 没有调用写历史库
        verify(memPool, times(3)).put(eq(handle), any(TVMemItem.class)); // 写入内存库
        verify(tvLog, times(2)).append(eq(handle), any(TVMemItem.class)); // 没有写日志

        when(tag.isPersist()).thenReturn(true);

        when(memPool.isHis(eq(handle), any(TV.class))).thenReturn(false); // 改回来
    }

    /**
     * Method: readReal()
     */
    @Test
    public void testReadReal() throws Exception {
        // 返回memPool所返回的
        when(memPool.getVal(handle)).thenReturn(null);
        assertNull(tagStore.readReal());

        TV tv = new TV(dateTime.getMillis(), value);
        when(memPool.getVal(handle)).thenReturn(tv);
        assertTrue(tv.equals(memPool.getVal(handle)));
    }

    /**
     * Method: readHis(long startTime, long endTime)
     */
    @Test
    public void testReadHisForStartTimeEndTime() throws Exception {
        // 1. 开始时间大于结束时间
        try {
            tagStore.readHis(dateTime.getMillis(), dateTime.minusSeconds(6).getMillis()); // 开始时间晚于结束时间异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000006".equals(e.getCode()));
        }

        // 2. 数据为空
        when(memPool.iterator(eq(handle), anyLong(), anyLong())).thenReturn(null);
        List<TagValue> tvList = tagStore.readHis(dateTime.getMillis(), dateTime.plusSeconds(60).getMillis());
        TestCase.assertNull(tvList);

        // 3. 正常数据
        TreeMap<Long, TV> result = new TreeMap<>();
        for (int i = 100; i < 200; i++) {
            TV t = new TV(dateTime.plusSeconds(i).getMillis(), value);
            result.put(t.getTimestamp(), t);
        }
        when(memPool.iterator(eq(handle), anyLong(), anyLong())).thenReturn(result);
        tvList = tagStore.readHis(dateTime.getMillis(), dateTime.plusSeconds(60).getMillis());
        assertTrue(tvList.size() == 100);
    }

    /**
     * Method: readHis(long startTime, long endTime, int interval)
     */
    @Test
    public void testReadHisForStartTimeEndTimeInterval() throws Exception {
        // 1. 开始时间大于结束时间
        try {
            tagStore.readHis(dateTime.getMillis(), dateTime.minusSeconds(6).getMillis(), 60 * 60); // 开始时间晚于结束时间异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000006".equals(e.getCode()));
        }

        // 2. interval合法性检查
        try {
            tagStore.readHis(dateTime.getMillis(), dateTime.plusSeconds(60).getMillis(), 0); // 开始时间晚于结束时间异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000010".equals(e.getCode()));
        }
        try {
            tagStore.readHis(dateTime.getMillis(), dateTime.plusSeconds(60).getMillis(), -1); // 开始时间晚于结束时间异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000010".equals(e.getCode()));
        }

        // 3. 数据为空
        when(memPool.iterator(eq(handle), anyLong(), anyLong())).thenReturn(null);
        List<TagValue> tvList = tagStore.readHis(dateTime.getMillis(), dateTime.plusSeconds(60).getMillis(), 10);
        TestCase.assertNull(tvList);

        // 4. 正常数据
        TreeMap<Long, TV> result = new TreeMap<>();
        for (int i = 0; i < 100; i++) {
            TV t = new TV(dateTime.plusSeconds(i).getMillis(), value);
            result.put(t.getTimestamp(), t);
        }
        when(memPool.iterator(eq(handle), anyLong(), anyLong())).thenReturn(result);

        // 4.1 正常
        tvList = tagStore.readHis(dateTime.getMillis(), dateTime.plusSeconds(100).getMillis(), 10);
        assertTrue(tvList.size() == 10);
    }

    /**
     * Method: readHis(long time, int interval)
     */
    @Test
    public void testReadHisForTimeInterval() throws Exception {
        TreeMap<Long, TV> result = new TreeMap<>();
        for (int i = 0; i < 1; i++) {
            TV t = new TV(dateTime.plusSeconds(i).getMillis(), value);
            result.put(t.getTimestamp(), t);
        }
        // 采样范围内有值
        when(memPool.iterator(eq(handle), anyLong(), anyLong())).thenReturn(result);
        long millis = dateTime.getMillis();

        TagValue tagValue = tagStore.readHis(millis, tolerance);
        assertNotNull(tagValue);
        verify(memPool, times(1)).iterator(eq(handle), anyLong(), anyLong());
        assertTrue(tagValue.getQuality() == TagValue.GOOD);

        // 找不到
        when(memPool.iterator(eq(handle), eq(millis - tolerance * 1000), eq(millis + tolerance * 1000))).thenReturn(null);
        tagValue = tagStore.readHis(millis, tolerance);
        assertNotNull(tagValue);
        verify(memPool, times(2)).iterator(eq(handle), anyLong(), anyLong());
        assertTrue(tagValue.getQuality() == TagValue.NO_VAL);
    }
}
