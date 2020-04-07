package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.disk.TVRowKey;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

/**
 * TVRowKey Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class TVRowKeyTest {

    private int handle = 11;
    private DateTime baseTime = new DateTime(2013, 8, 26, 16, 0, 0, 0);
    private TVRowKey rowKey = new TVRowKey(handle, baseTime.getMillis());

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getKey()
     */
    @Test
    public void testGetKey() throws Exception {
        assertTrue(rowKey.getHandle() == handle);
        assertTrue(rowKey.getBaseTime() == baseTime.getMillis());

        assertTrue(new TVRowKey(rowKey.getKey()).equals(rowKey));

        try {
            new TVRowKey(null); // 值不能为空异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            Assert.assertTrue("E-000003".equals(e.getCode()));
        }

        try {
            new TVRowKey(new byte[0]); // 值不能为空异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            Assert.assertTrue("E-000003".equals(e.getCode()));
        }
    }

} 
