package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.Application;
import com.typesafe.config.Config;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-10-17
 */
public class TVDBFactory {
    public static TVDB createTVDB(Application app) {
        Config config = app.getConfig();
        PersistDB persistDB;
        switch (config.getString("disk.db")) {
            //                case "bangdb":
            //                    break;
            //                case "bdb":
            //                    break;
            case "leveldb":
            default:
                persistDB = new LevelTVDB(app);
        }

        return new DefaultDB(app, persistDB);
    }

    public static StoreStrategy createStrategy(String strategy, Long onlineTime) {
        switch (strategy) {
            case "yearly":
            case "always":
                return new YearlyStore(onlineTime);
            case "monthly":
            default:
                return new MonthlyStore(onlineTime);
        }
    }
}
