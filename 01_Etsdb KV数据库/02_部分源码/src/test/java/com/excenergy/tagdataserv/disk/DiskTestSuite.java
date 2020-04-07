package com.excenergy.tagdataserv.disk;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-10-17
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({DefaultDBTest.class, LevelTVDBTest.class, MonthlyStoreTest.class, TVMapperTest.class, //
        TVRowKeyTest.class, TVRowValueTest.class, YearlyStoreTest.class})
public class DiskTestSuite {
}
