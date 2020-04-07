package com.excenergy.tagdataserv;

import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.disk.TVDB;
import com.excenergy.tagdataserv.log.TVLog;
import com.excenergy.tagdataserv.mem.TVMemItem;
import com.excenergy.tagdataserv.mem.TVMemPool;
import com.excenergy.tagmeta.RealTag;
import com.excenergy.tagmeta.Tag;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * PreProcesser Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class PreProcesserTest {

    private final TagFactory tagFactory;
    private final TVMemPool memPool;
    private final TVDB tvdb;
    private final TVLog tvLog;
    private final RealTag tag;
    private final Integer handle = 111;
    private final Integer tolerance = 600;
    private PreProcesser preProcesser;
    private final Integer preProcessInterval = 900;
    private DateTime dateTime = new DateTime(2013, 10, 10, 10, 10, 10, 0);
    private byte[] value = new byte[]{(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};

    public PreProcesserTest() {
        tag = mock(RealTag.class);
        when(tag.getHandle()).thenReturn(handle);
        when(tag.getName()).thenReturn("r.123");
        when(tag.getCacheMax()).thenReturn(100);
        when(tag.getMergeInterval()).thenReturn(Tag.HOURLY);
        when(tag.isPersist()).thenReturn(true);
        when(tag.getTolerance()).thenReturn(tolerance);
        when(tag.getPrInterval()).thenReturn(preProcessInterval);
        when(tag.isCumulativeVal()).thenReturn(true);
        when(tag.getValType()).thenReturn(Tag.DOUBLE);

        tagFactory = mock(TagFactory.class);
        when(tagFactory.get(anyInt())).thenReturn(tag);
        when(tagFactory.get("pr.123", false)).thenReturn(tag);
        when(tagFactory.get("phr.123", false)).thenReturn(tag);
        when(tagFactory.get("pdr.123", false)).thenReturn(tag);

        memPool = mock(TVMemPool.class);

        tvdb = mock(TVDB.class);
        tvLog = mock(TVLog.class);

        Application app = new Application();
        app.setTagFactory(tagFactory);
        app.setMemPool(memPool);
        app.setTvdb(tvdb);
        app.setTvLog(tvLog);
        preProcesser = new PreProcesser(app, tag);
    }

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testProcess() throws Exception {
        preProcesser.process(new TagValue(dateTime, TagValue.DOUBLE, 100.0, TagValue.GOOD));
        preProcesser.process(new TagValue(dateTime.plusMinutes(5), TagValue.DOUBLE, 101.0, TagValue.GOOD));
        preProcesser.process(new TagValue(dateTime.plusMinutes(10), TagValue.DOUBLE, 102.0, TagValue.GOOD));
        preProcesser.process(new TagValue(dateTime.plusMinutes(15), TagValue.DOUBLE, 103.0, TagValue.GOOD));
        preProcesser.process(new TagValue(dateTime.plusMinutes(20), TagValue.DOUBLE, 104.0, TagValue.GOOD));
        preProcesser.process(new TagValue(dateTime.plusMinutes(25), TagValue.DOUBLE, 105.0, TagValue.GOOD));
        preProcesser.process(new TagValue(dateTime.plusMinutes(30), TagValue.DOUBLE, 106.0, TagValue.GOOD));
    }

    /**
     * Method: process(TV tv)
     */
//    @Test
//    public void testProcess() throws Exception {
//        TagValue tagValue;
//
//        // 第1个值
//        tagValue = new TagValue(dateTime, TagValue.DOUBLE, 100.0, TagValue.GOOD);
//        preProcesser.process(trans(tagValue));
//        verify(tagFactory, times(1)).get(handle);
//        verify(memPool, times(0)).put(eq(handle), any(TVMemItem.class));
//        assertTrue(preProcesser.getCount() == 0);
//        assertTrue(preProcesser.getAverage() == -1);
//        assertTrue(preProcesser.getRealVal() == 100.0);
//        assertTrue(preProcesser.getLatestPRTime() == dateTime.getMillis());
//        assertTrue(preProcesser.getPrValue() == 0);
//
//        // 测试老于实时值
//        tagValue = new TagValue(dateTime.minusSeconds(preProcessInterval), TagValue.DOUBLE, 90, TagValue.GOOD);
//        preProcesser.process(trans(tagValue));
//        verify(tagFactory, times(2)).get(handle);
//        verify(memPool, times(0)).put(eq(handle), any(TVMemItem.class));
//        assertTrue(preProcesser.getCount() == 0);
//        assertTrue(preProcesser.getAverage() == -1);
//        assertTrue(preProcesser.getRealVal() == 100.0);
//        assertTrue(preProcesser.getLatestPRTime() == dateTime.getMillis());
//        assertTrue(preProcesser.getPrValue() == 0);
//
//        // 测试小于1个周期
//        tagValue = new TagValue(dateTime.plusSeconds(preProcessInterval / 3), TagValue.DOUBLE, 130, TagValue.GOOD);
//        preProcesser.process(trans(tagValue));
//        verify(tagFactory, times(3)).get(handle);
//        verify(memPool, times(0)).put(eq(handle), any(TVMemItem.class));
//        assertTrue(preProcesser.getCount() == 0);
//        assertTrue(preProcesser.getAverage() == -1);
//        assertTrue(preProcesser.getRealVal() == 130.0);
//        assertTrue(preProcesser.getLatestPRTime() == dateTime.getMillis());
//        assertTrue(preProcesser.getPrValue() == 30);
//
//        tagValue = new TagValue(dateTime.plusSeconds(preProcessInterval * 2 / 3), TagValue.DOUBLE, 170, TagValue.GOOD);
//        preProcesser.process(trans(tagValue));
//        verify(tagFactory, times(4)).get(handle);
//        verify(memPool, times(0)).put(eq(handle), any(TVMemItem.class));
//        assertTrue(preProcesser.getCount() == 0);
//        assertTrue(preProcesser.getAverage() == -1);
//        assertTrue(preProcesser.getRealVal() == 170.0);
//        assertTrue(preProcesser.getLatestPRTime() == dateTime.getMillis());
//        assertTrue(preProcesser.getPrValue() == 70);
//
//        // 测试大于等于1个周期，小于2个周期
//        tagValue = new TagValue(dateTime.plusSeconds(preProcessInterval), TagValue.DOUBLE, 200, TagValue.GOOD);
//        preProcesser.process(trans(tagValue));
//        verify(tagFactory, times(5)).get(handle);
//        verify(memPool, times(1)).put(eq(handle), any(TVMemItem.class));
//        assertTrue(preProcesser.getCount() == 1);
//        assertTrue(preProcesser.getAverage() == 100);
//        assertTrue(preProcesser.getRealVal() == 200.0);
//        assertTrue(preProcesser.getLatestPRTime() == dateTime.plusSeconds(preProcessInterval).getMillis());
//        assertTrue(preProcesser.getPrValue() == 0);
//
//        // 测试大于2个周期
//        tagValue = new TagValue(dateTime.plusSeconds(preProcessInterval * 5), TagValue.DOUBLE, 800, TagValue.GOOD);
//        preProcesser.process(trans(tagValue));
//        verify(tagFactory, times(6)).get(handle);
//        verify(memPool, times(1)).put(eq(handle), any(TVMemItem.class));
//        assertTrue(preProcesser.getCount() == 1);
//        assertTrue(preProcesser.getAverage() == 100);
//        assertTrue(preProcesser.getRealVal() == 800.0);
//        assertTrue(preProcesser.getLatestPRTime() == dateTime.plusSeconds(preProcessInterval * 5).getMillis());
//        assertTrue(preProcesser.getPrValue() == 0);
//
//        // 半个周期后上来一个点
//        tagValue = new TagValue(dateTime.plusSeconds((int) (preProcessInterval * 5.5)), TagValue.DOUBLE, 850, TagValue.GOOD);
//        preProcesser.process(trans(tagValue));
//        verify(tagFactory, times(7)).get(handle);
//        verify(memPool, times(1)).put(eq(handle), any(TVMemItem.class));
//        assertTrue(preProcesser.getCount() == 1);
//        assertTrue(preProcesser.getAverage() == 100);
//        assertTrue(preProcesser.getRealVal() == 850.0);
//        assertTrue(preProcesser.getLatestPRTime() == dateTime.plusSeconds(preProcessInterval * 5).getMillis());
//        assertTrue(preProcesser.getPrValue() == 50);
//
//        // 满足1个周期后。。。
//        tagValue = new TagValue(dateTime.plusSeconds((preProcessInterval * 6)), TagValue.DOUBLE, 1000, TagValue.GOOD);
//        preProcesser.process(trans(tagValue));
//        verify(tagFactory, times(8)).get(handle);
//        verify(memPool, times(2)).put(eq(handle), any(TVMemItem.class));
//        assertTrue(preProcesser.getCount() == 2);
//        assertTrue(preProcesser.getAverage() == 150); // (100 + 200) / 2 = 150
//        assertTrue(preProcesser.getRealVal() == 1000.0);
//        assertTrue(preProcesser.getLatestPRTime() == dateTime.plusSeconds(preProcessInterval * 6).getMillis());
//        assertTrue(preProcesser.getPrValue() == 0);
//    }

    /**
     * Method: write(RealTag realTag, double value)
     */
    @Test
    public void testWriteForRealTagValue() throws Exception {
        //TODO: Test goes here...
/*
try {
   Method method = PreProcesser.getClass().getMethod("write", RealTag.class, double.class);
   method.setAccessible(true);
   method.invoke(<Object>, <Parameters>);
} catch(NoSuchMethodException e) {
} catch(IllegalAccessException e) {
} catch(InvocationTargetException e) {
}
*/
    }

    /**
     * Method: write(Tag tag, TagValue pr)
     */
    @Test
    public void testWriteForTagPr() throws Exception {
        //TODO: Test goes here...
/* 
try { 
   Method method = PreProcesser.getClass().getMethod("write", Tag.class, TagValue.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

} 
