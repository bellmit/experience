package com.excenergy.tagdataserv.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-10-17
 */
public class LatestPersist {
    private static final Logger logger = LoggerFactory.getLogger(LatestPersist.class);
    private static int version = 0;

    public static synchronized void setVersion(int version) {
        if (logger.isInfoEnabled()) {
            logger.info("persistFinish:" + version);
        }
        LatestPersist.version = version;
    }

    public static synchronized int getVersion() {
        return version;
    }
}
