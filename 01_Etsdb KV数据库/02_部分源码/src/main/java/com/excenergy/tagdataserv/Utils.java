package com.excenergy.tagdataserv;

import org.joda.time.DateTime;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-29
 */
public class Utils {
    public static String timeFormat(long timestamp) {
        return new DateTime(timestamp).toString("yyyy-MM-dd HH:mm:ss.SSS");
    }
}
