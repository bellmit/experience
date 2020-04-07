package com.excenergy.tagdataserv.disk;

import org.junit.Test;

/**
 * LevelDB Tester.
 * God Bless You!
 * Author: Li Pengpeng
 */
public class PureBangDBTest {
    @Test
    public void testPutGetDelete() throws Exception {
        //        System.loadLibrary("bangdbjava");
        //        Database db = new DatabaseImpl("mydb", TRANSACTIONTYPE.DB_OPTIMISTIC_TRANSACTION);
        //        Table tbl = db.getTable("mytbl", DBACCESS.OPENCREATE);
        //        if (tbl == null) {
        //            System.out.println("table null error");
        //            return;
        //        }
        //        Connection conn = tbl.getConnection();
        //        if (conn == null) {
        //            System.out.println("connection null error");
        //            return;
        //        }
        //
        //        boolean success = true;
        //
        //	/* insert a value and get it */
        //        String k = "my key\0sjsj";
        //        String v = "The test value";
        //        byte[] key = k.getBytes();
        //        byte[] val = v.getBytes();
        //
        //        if (conn.put(key, val, INSERTOPTIONS.INSERT_UNIQUE) < 0) {
        //            success = false;
        //            System.out.println("Put failed");
        //        }
        //
        //        byte[] retval = conn.get(key);
        //        if (retval == null)
        //            System.out.println("Get Failed.");
        //        else {
        //            if (!Arrays.equals(retval, val))
        //                System.out.println("Get Mismatch.");
        //        }
    }
} 
