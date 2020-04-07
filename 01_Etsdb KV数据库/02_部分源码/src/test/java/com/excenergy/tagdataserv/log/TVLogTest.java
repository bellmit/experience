package com.excenergy.tagdataserv.log;

import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.mem.TVMemItem;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TVLog Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class TVLogTest {
//    TVLog tvLog = TVLog.getInstance();

    private DateTime dateTime = new DateTime(2013, 9, 5, 10, 42, 18, 0);
    private byte[] value = {(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};
    private TV tv = new TV(dateTime.getMillis(), value);
    private TVMemItem item = new TVMemItem(tv);

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getInstance()
     */
    @Test
    public void testGetInstance() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: append(int handle, TVMemItem item)
     */
    @Test
    public void testAppend() throws Exception {
//        tvLog.append(133, item);
//        assertTrue(tvLog.handleQueue.take() == 133);
//        assertTrue(tvLog.itemQueue.take() == item);
    }

    /**
     * Method: load()
     */
    @Test
    public void testLoad() throws Exception {
        //        Map<Integer, List<TV>> tvMap = tvLog.load();
        //        for (int i = 0; i < 1000; i++) {
        //            TV tv = new TV(dateTime.plusSeconds(i).getMillis(), value);
        //            assertTrue(tvMap.get(133).contains(tv));
        //        }
    }

    /**
     * Method: read()
     */
    @Test
    public void testRead() throws Exception {
        //        DateTime dateTime = new DateTime(2013, 9, 5, 10, 42, 18, 0);
        //        byte[] value = {(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};
        //
        //        try {
        //            Method method = tvLog.getClass().getDeclaredMethod("read", String.class);
        //            method.setAccessible(true);
        //            Map<Integer, List<TV>> tvMap = (Map<Integer, List<TV>>) method.invoke(tvLog, "0");
        //            for (int i = 0; i < 1000; i++) {
        //                TV tv = new TV(dateTime.plusSeconds(i).getMillis(), value);
        //                assertTrue(tvMap.get(133).contains(tv));
        //            }
        //        } catch (NoSuchMethodException e) {
        //        e.printStackTrace();
        //        assertFalse(true);
        //        } catch (IllegalAccessException e) {
        //        } catch (InvocationTargetException e) {
        //        }
    }
} 
