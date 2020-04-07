package com.excenergy.tagdataserv.mem;

import com.excenergy.tagdataserv.TV;
import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.mem.TVMemItem;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * TVMemItem Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class TVMemItemTest {

    private DateTime dateTime = new DateTime(2013, 9, 5, 10, 42, 18, 0);
    private byte[] value = {(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};
    private TV tv;

    @Before
    public void before() throws Exception {
        tv = new TV(dateTime.getMillis(), value);
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testNullPara() {
        try {
            new TVMemItem(null, false);// test null
        } catch (TagDataException e) {
            assertNotNull(e);
            assertTrue(e.getCode().equals("E-000008"));
        }
    }

    /**
     * Method: getTv()
     */
    @Test
    public void testGetTv() throws Exception {
        assertTrue(new TVMemItem(tv, true).getTv().equals(tv));
    }

    /**
     * Method: getVersion()
     */
    @Test
    public void testGetVersion() throws Exception {
        TVMemItem item = new TVMemItem(tv, false);
        assertTrue(new TVMemItem(tv, false).getVersion() == item.getVersion() + 1);
        assertTrue(new TVMemItem(tv, false).getVersion() == item.getVersion() + 2);
        new TVMemItem(tv, true);
        assertTrue(new TVMemItem(tv, false).getVersion() == item.getVersion() + 3);
    }

    /**
     * Method: setHasPersist()
     */
    @Test
    public void testSetHasPersist() throws Exception {
        TVMemItem item = new TVMemItem(tv, false);
        assertFalse(item.hasPersist());
        item.setHasPersist();
        assertTrue(item.hasPersist());
    }

    /**
     * Method: hasPersist()
     */
    @Test
    public void testHasPersist() throws Exception {
        TVMemItem item = new TVMemItem(tv, false);
        assertFalse(item.hasPersist());
        item.setHasPersist();
        assertTrue(item.hasPersist());

        item = new TVMemItem(tv, true);
        assertTrue(item.hasPersist());
    }
}
