package com.excenergy.tagdataserv.log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * PersistThread Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class PersistThreadTest {

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
//        RealTag tag = new RealTag(3, "r.123");
//        tag.setMergeInterval(Tag.HOURLY);
//        TagFactory.getInstance().put(tag);
//        File file = new File("data/log/0");
//        assertTrue(file.exists());
//        DateTime dateTime = new DateTime(2013, 9, 5, 10, 42, 18, 0);
//        byte[] value = {(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};
//        HashMap<Integer, List<TV>> map = new HashMap<>();
//        ArrayList<TV> list = new ArrayList<>();
//        list.add(new TV(dateTime.getMillis(), value));
//        list.add(new TV(dateTime.plusSeconds(1).getMillis(), value));
//        list.add(new TV(dateTime.plusSeconds(2).getMillis(), value));
//        list.add(new TV(dateTime.plusSeconds(3).getMillis(), value));
//        list.add(new TV(dateTime.plusSeconds(4).getMillis(), value));
//        list.add(new TV(dateTime.plusSeconds(5).getMillis(), value));
//        list.add(new TV(dateTime.plusSeconds(6).getMillis(), value));
//        list.add(new TV(dateTime.plusSeconds(7).getMillis(), value));
//        map.put(3, list);
//        PersistThread persistThread = new PersistThread();
//        persistThread.getPersistQueue().add(new PersistTask("0", -1, map));
//        persistThread.persist();
//        assertFalse(file.exists());
    }

    /**
     * Method: addPersistListener(PersistListener listener)
     */
    @Test
    public void testAddPersistListener() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: isPersisting()
     */
    @Test
    public void testIsPersisting() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: getPersistQueue()
     */
    @Test
    public void testGetPersistQueue() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: remove(String fileName)
     */
    @Test
    public void testRemove() throws Exception {
        //TODO: Test goes here...
/* 
try { 
   Method method = PersistThread.getClass().getMethod("remove", String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

} 
