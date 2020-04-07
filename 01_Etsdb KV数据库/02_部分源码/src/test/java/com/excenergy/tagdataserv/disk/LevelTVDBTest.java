package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.TagFactory;
import com.excenergy.tagmeta.RealTag;
import com.excenergy.tagmeta.Tag;
import com.excenergy.tagmeta.VirtualTag;
import org.iq80.leveldb.DB;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * LevelTVDB Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class LevelTVDBTest {

    private LevelTVDB db;
    private TagFactory tagFactory;

    @Before
    public void before() throws Exception {
        tagFactory = mock(TagFactory.class);
//        db = TVDBFactory.createLevelDB(ConfigFactory.load(), tagFactory, new DateTime(2010, 12, 12, 12, 12, 12, 0).getMillis());
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: put(TVRowKey key, TVRowValue value)
     */
    @Test
    public void testPutGet() throws Exception {
//        int handle = 111;
//        Tag tag = new RealTag();
//        tag.setHandle(handle);
//        tag.setMergeInterval(Tag.HOURLY);
//        TagFactory.getInstance().put(tag);
//
//        ArrayList<TV> tvs = new ArrayList<>();
//        ByteBuffer buffer = ByteBuffer.allocate(9);
//        buffer.put((byte) 192);
//        buffer.putDouble(19.8);
//        DateTime dt = new DateTime(2012, 8, 26, 8, 51, 18, 0);
//
//        tvs.add(new TV(dt.getMillis(), buffer.array()));
//        tvs.add(new TV(dt.plusHours(1).getMillis(), buffer.array()));
//        TV e1 = new TV(dt.plusDays(1).getMillis(), buffer.array());
//        tvs.add(e1);
//        tvs.add(new TV(dt.plusMonths(1).getMillis(), buffer.array()));
//        TV e = new TV(dt.plusYears(1).getMillis(), buffer.array());
//        tvs.add(e);
//
//        db.put(handle, tvs);
//
//        TV latest = db.get(handle);
//        assertTrue(latest.equals(e));
//
//        TV tv = db.get(handle, e1.getTimestamp());
//        assertTrue(tv.equals(e1));
    }

    /**
     * Method: getPath(TVRowKey key)
     */
    @Test
    public void testGetPath() throws Exception {
        try {
            Method method = LevelTVDB.class.getDeclaredMethod("getPath", TVRowKey.class);
            method.setAccessible(true);

            int handle = 111;
            Tag tag = new RealTag();
            tag.setHandle(handle);
            tag.setName("r.123");
            tag.setMergeInterval(Tag.HOURLY);

            when(tagFactory.get(handle)).thenReturn(tag);

            DateTime dt = new DateTime(2012, 8, 26, 8, 51, 18, 0);
            TVRowKey key = new TVRowKey(handle, dt.getMillis());
            Object invoke = method.invoke(db, key);
            assertNotNull(invoke);
            assertTrue(invoke instanceof Path);
            assertTrue(invoke.toString().contains(File.separator + "r" + File.separator));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            assertFalse(true);
        } catch (IllegalAccessException e) {
        }
    }

    /**
     * Method: getBasePath(int handle)
     */
    @Test
    public void testGetBasePath() throws Exception {
        try {
            Method method = LevelTVDB.class.getDeclaredMethod("getBasePath", int.class);
            method.setAccessible(true);

            int handle = 111;
            Tag tag = new RealTag();
            tag.setHandle(handle);
            tag.setMergeInterval(Tag.HOURLY);

            when(tagFactory.get(handle)).thenReturn(tag);

            Object invoke = method.invoke(db, handle);
            assertNotNull(invoke);
            assertTrue(invoke instanceof Path);
            assertTrue(invoke.toString().endsWith("r"));

            handle = 112;
            tag = new VirtualTag();
            tag.setHandle(handle);
            tag.setMergeInterval(Tag.HOURLY);

            when(tagFactory.get(handle)).thenReturn(tag);

            invoke = method.invoke(db, handle);
            assertNotNull(invoke);
            assertTrue(invoke instanceof Path);
            assertTrue(invoke.toString().endsWith("v"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            assertFalse(true);
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    /**
     * Method: getLevelDB(Path path)
     */
    @Test
    public void testGetLevelDB() throws Exception {
        try {
            Method method = LevelTVDB.class.getDeclaredMethod("getLevelDB", Path.class);
            method.setAccessible(true);

            Method basePathMethod = LevelTVDB.class.getDeclaredMethod("getBasePath", int.class);
            basePathMethod.setAccessible(true);

            int handle = 111;
            Tag tag = new RealTag();
            tag.setHandle(handle);
            tag.setMergeInterval(Tag.HOURLY);

            when(tagFactory.get(handle)).thenReturn(tag);

            Path path = (Path) basePathMethod.invoke(db, handle);
            Object invoke = method.invoke(db, path);
            assertNotNull(invoke);
            assertTrue(invoke instanceof DB);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            assertFalse(true);
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

} 
