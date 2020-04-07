package com.excenergy.tagdataserv.log;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * WriteLogThread Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class WriteLogThreadTest {
//    TVLog tvLog = TVLog.getInstance();

    private DateTime dateTime = new DateTime(2013, 9, 5, 10, 42, 18, 0);
    private byte[] value = {(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: run()
     */
    @Test
    public void testRun() throws Exception {
        //        for (int i = 0; i < 1000; i++) {
        //            tvLog.handleQueue.offer(3);
        //            tvLog.itemQueue.offer(new TVMemItem(new TV(dateTime.plusSeconds(i).getMillis(), value)));
        //        }
        //        new WriteLogThread().run();
    }
} 
