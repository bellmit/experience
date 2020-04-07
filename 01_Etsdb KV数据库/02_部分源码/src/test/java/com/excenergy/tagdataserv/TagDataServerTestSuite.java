package com.excenergy.tagdataserv;

import com.excenergy.tagdataserv.disk.DiskTestSuite;
import com.excenergy.tagdataserv.mem.MemTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-10-17
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TagFactoryTest.class, //
        TagStoreTest.class, //
        DiskTestSuite.class,//
//        LogTestSuite.class,//
        MemTestSuite.class})
public class TagDataServerTestSuite {


}
