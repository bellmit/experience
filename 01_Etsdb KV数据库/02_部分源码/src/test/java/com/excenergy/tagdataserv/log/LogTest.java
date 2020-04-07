package com.excenergy.tagdataserv.log;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-06
 */
public class LogTest {
//    public static final int HANDLE = 133;
//    TVLog tvLog;
//    private DateTime dateTime = new DateTime(2013, 9, 5, 10, 42, 18, 0);
//    private byte[] value = {(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};
//
//    @Before
//    public void before() throws Exception {
//    }
//
//    @After
//    public void after() throws Exception {
//    }
//
//    @Test
//    public void testAll() throws Exception {
//        TagDataServer.getInstance().start();
//        tvLog = TVLog.getInstance();
//        Tag tag = new RealTag(HANDLE, "r.123");
//        tag.setCacheMax(10000);
//        tag.setPersist(true);
//        tag.setMergeInterval(Tag.HOURLY);
//        TagFactory.getInstance().put(tag);
//
//        testWriteLog();
//        Thread.sleep(1000);
//        testReadLog();
//        testLoadLog();
//        testPersist();
//    }
//
//    public void testWriteLog() throws Exception {
//        for (int i = 0; i < 1000; i++) {
//            tvLog.append(HANDLE, new TVMemItem(new TV(dateTime.plusSeconds(i).getMillis(), value)));
//        }
//    }
//
//    public void testReadLog() throws Exception {
//        try {
//            Method method = tvLog.getClass().getDeclaredMethod("read", String.class);
//            method.setAccessible(true);
//            Map<Integer, List<TV>> tvMap = (Map<Integer, List<TV>>) method.invoke(tvLog, "0");
//            for (int i = 0; i < 1000; i++) {
//                TV tv = new TV(dateTime.plusSeconds(i).getMillis(), value);
//                assertTrue(tvMap.get(HANDLE).contains(tv));
//            }
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//            assertFalse(true);
//        } catch (IllegalAccessException e) {
//        } catch (InvocationTargetException e) {
//        }
//    }
//
//    private void testLoadLog() {
//        Map<Integer, List<TV>> tvMap = tvLog.load();
//        for (int i = 0; i < 1000; i++) {
//            TV tv = new TV(dateTime.plusSeconds(i).getMillis(), value);
//            assertTrue(tvMap.get(HANDLE).contains(tv));
//        }
//    }
//
//    public void testPersist() throws Exception {
////        RealTag tag = new RealTag(HANDLE, "r.123");
////        tag.setMergeInterval(Tag.HOURLY);
////        TagFactory.getInstance().put(tag);
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
//        map.put(HANDLE, list);
//        PersistThread persistThread = new PersistThread();
//        persistThread.getPersistQueue().add(new PersistTask("0", -1, map));
//        persistThread.persist();
//        assertFalse(file.exists());
//    }
//
//    public static void main(String[] args) {
//        //        Tag tag = new RealTag(133, "r.123");
//        //        tag.setCacheMax(100);
//        //        TagFactory.getInstance().put(tag);
//        //        DateTime dateTime = new DateTime(2013, 9, 5, 10, 42, 18, 0);
//        //        byte[] value = {(byte) 192, 2, 3, 4, 5, 6, 7, 8, 9};
//        //
//        //        TVLog tvLog = TVLog.getInstance();
//        //        PersistThread persistThread = new PersistThread();
//        //        Executors.newSingleThreadExecutor().execute(persistThread);
//        //        Executors.newSingleThreadExecutor().execute(new WriteLogThread(persistThread));
//        //        for (int i = 0; i < 1000; i++) {
//        //            tvLog.append(133, new TVMemItem(new TV(dateTime.plusSeconds(i).getMillis(), value)));
//        //        }
//    }
}
