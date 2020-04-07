package com.excenergy.tagdataserv.stats;

import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.TagStorePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-09-09
 */
public class StatsCollector implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(StatsCollector.class);

    private final StatsItems items;
    private TagStorePool tagStorePool;

    public StatsCollector(TagStorePool tagStorePool, StatsItems statsItems) {
        this.tagStorePool = tagStorePool;
        this.items = statsItems;
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setName("Stats-Collector-Thread");
            if (logger.isDebugEnabled()) {
                logger.debug("start stats collector");
            }

            Map<Integer, TagValue> stats = items.getStats();
            for (Map.Entry<Integer, TagValue> entry : stats.entrySet()) {
                tagStorePool.getTagStore(entry.getKey()).writeVal(entry.getValue());
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("Something unexpected occurred.", e);
            }
        }
    }
}
