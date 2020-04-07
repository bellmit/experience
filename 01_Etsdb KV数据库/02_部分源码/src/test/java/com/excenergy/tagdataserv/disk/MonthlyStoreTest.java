package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.TagDataException;
import com.excenergy.tagdataserv.TagDataServer;
import com.excenergy.tagdataserv.disk.MonthlyStore;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * MonthlyStore Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class MonthlyStoreTest {

    private MonthlyStore store;
    private long online;

    @Before
    public void before() throws Exception {
        online = new DateTime(2010,10,10,10,10,0).getMillis();
        store = new MonthlyStore(online);
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: path(Path basePath, long timestamp)
     */
    @Test
    public void testPath() throws Exception {
        DateTime dateTime = new DateTime(2013, 8, 28, 16, 5, 18, 500);
        Path path = store.path(Paths.get("tmp"), dateTime.getMillis());
        assertTrue(path.toString().equals("tmp\\2013\\8"));

        try {
            store.path(null, dateTime.getMillis()); // 值不能为空异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000008".equals(e.getCode()));
        }

        try {
            store.path(Paths.get("tmp"), online); // 时间错误异常
            assertTrue(true);
            store.path(Paths.get("tmp"), online - 1); // 时间错误异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000001".equals(e.getCode()));
        }

        try {
            store.path(Paths.get("tmp"), System.currentTimeMillis() - 10000); // 时间错误异常
            assertTrue(true);
            store.path(Paths.get("tmp"), System.currentTimeMillis() + 10000); // 时间错误异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000001".equals(e.getCode()));
        }
    }

    /**
     * Method: next(Path path)
     */
    @Test
    public void testNext() throws Exception {
        DateTime dateTime = new DateTime(2012, 11, 28, 16, 5, 18, 500);
        Path path = store.path(Paths.get("tmp"), dateTime.getMillis());
        assertTrue(path.toString().equals("tmp\\2012\\11"));
        path = store.next(path);
        assertTrue(path.toString().equals("tmp\\2012\\12"));
        path = store.next(path);
        assertTrue(path.toString().equals("tmp\\2013\\1"));

        try {
            store.next(null); // 值不能为空异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000008".equals(e.getCode()));
        }
    }

    /**
     * Method: prev(Path path)
     */
    @Test
    public void testPrev() throws Exception {
        DateTime dateTime = new DateTime(2013, 2, 28, 16, 5, 18, 500);
        Path path = store.path(Paths.get("tmp"), dateTime.getMillis());
        assertTrue(path.toString().equals("tmp\\2013\\2"));
        path = store.prev(path);
        assertTrue(path.toString().equals("tmp\\2013\\1"));
        path = store.prev(path);
        assertTrue(path.toString().equals("tmp\\2012\\12"));

        try {
            store.prev(null); // 值不能为空异常
            fail("Expected an TagDataException to be thrown");
        } catch (TagDataException e) {
            assertTrue("E-000008".equals(e.getCode()));
        }
    }
}
