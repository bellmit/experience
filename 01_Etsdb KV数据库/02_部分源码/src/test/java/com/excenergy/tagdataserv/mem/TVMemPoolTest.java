package com.excenergy.tagdataserv.mem;

import com.excenergy.tagdataserv.Application;
import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagdataserv.disk.TVDB;
import com.excenergy.tagdataserv.mem.TVMemItem;
import com.excenergy.tagdataserv.mem.TVMemPool;
import com.excenergy.tagmeta.RealTag;
import com.excenergy.tagmeta.Tag;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.NavigableMap;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TVMemPool Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class TVMemPoolTest {

    private DateTime dateTime = new DateTime(2013, 9, 5, 10, 42, 18, 0);
    private byte[] value = {(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};
    private TV tv = new TV(dateTime.getMillis(), value);

    private TVMemPool memPool;
    private RealTag tag;
    private TagFactory tagFactory;

    @Before
    public void before() throws Exception {
        tag = new RealTag(3, "r.123");
        tag.setCacheMax(100);
        tag.setMergeInterval(Tag.HOURLY);
        tagFactory = mock(TagFactory.class);
        when(tagFactory.get(anyInt())).thenReturn(tag);

        Application app = new Application();
        app.setTagFactory(tagFactory);
        app.setTvdb(mock(TVDB.class));
        app.setOnlineTime(new DateTime(2010, 10, 10, 10, 10, 0).getMillis());
        memPool = new TVMemPool(app);
        TV val = memPool.getVal(tag.getHandle());
        if (val != null) {
            tv = new TV(val.getTimestamp() + 10000, value);
        }
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: put(int handle, TVMemItem v)
     */
    @Test
    public void testPut() throws Exception {
        memPool.put(tag.getHandle(), new TVMemItem(tv, true));
        assertTrue(memPool.getVal(tag.getHandle()).equals(tv));
    }

    /**
     * Method: getVal(int handle)
     */
    @Test
    public void testGetValHandle() throws Exception {
        memPool.put(tag.getHandle(), new TVMemItem(tv, true));
        assertTrue(memPool.getVal(tag.getHandle()).equals(tv));
    }

    /**
     * Method: getVal(int handle, long timestamp)
     */
    @Test
    public void testGetValForHandleTimestamp() throws Exception {
        memPool.put(tag.getHandle(), new TVMemItem(tv, true));
        assertTrue(memPool.getVal(tag.getHandle(), tv.getTimestamp()).equals(tv));
    }

    /**
     * Method: iterator(int handle, long startTime, long endTime)
     */
    @Test
    public void testIterator() throws Exception {
        memPool.put(tag.getHandle(), new TVMemItem(new TV(dateTime.getMillis(), value), false));
        memPool.put(tag.getHandle(), new TVMemItem(new TV(dateTime.plusSeconds(1).getMillis(), value), false));
        memPool.put(tag.getHandle(), new TVMemItem(new TV(dateTime.plusSeconds(2).getMillis(), value), false));
        memPool.put(tag.getHandle(), new TVMemItem(new TV(dateTime.plusSeconds(3).getMillis(), value), false));
        memPool.put(tag.getHandle(), new TVMemItem(new TV(dateTime.plusSeconds(4).getMillis(), value), false));
        memPool.put(tag.getHandle(), new TVMemItem(new TV(dateTime.plusSeconds(5).getMillis(), value), false));
        memPool.put(tag.getHandle(), new TVMemItem(new TV(dateTime.plusSeconds(6).getMillis(), value), false));
        NavigableMap<Long, TV> iterator = memPool.iterator(tag.getHandle(), dateTime.getMillis(), dateTime.plusSeconds(6).getMillis());
        assertTrue(iterator.size() == 7);
        assertTrue(iterator.firstKey() == dateTime.getMillis());
        assertTrue(iterator.lastKey() == dateTime.plusSeconds(6).getMillis());
    }
} 
