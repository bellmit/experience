package com.excenergy.tagdataserv.disk;

import com.excenergy.tagdataserv.TagDataException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static com.excenergy.tagdataserv.TagDataException.getMsg;
import static com.excenergy.tagdataserv.Utils.timeFormat;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-27
 */
public class YearlyStore implements StoreStrategy {
    private static final Logger logger = LoggerFactory.getLogger(YearlyStore.class);
    private final long onlineTime;

    public YearlyStore(Long onlineTime) {
        this.onlineTime = onlineTime;
    }

    @Override
    public Path path(Path basePath, long timestamp) {
        if (basePath == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "basePath", "YearlyStore.path");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        DateTime time = new DateTime(timestamp);
        if (time.getMillis() < onlineTime) {
            String code = "E-000001";
            String msg = String.format(getMsg(code), timeFormat(onlineTime), timeFormat(System.currentTimeMillis()), timeFormat(timestamp));
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        return basePath.resolve(String.valueOf(time.getYear()));
    }

    @Override
    public Path next(Path path) {
        if (path == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "path", "YearlyStore.next");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        Integer year = Integer.valueOf(path.getFileName().toString());
        path = path.getParent();
        DateTime time = new DateTime(year, 1, 1, 0, 0, 0, 0).plusYears(1);
        if (time.getMillis() > System.currentTimeMillis()) {
            return null;
        }
        return path.resolve(String.valueOf(time.getYear()));
    }

    @Override
    public Path prev(Path path) {
        if (path == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "path", "YearlyStore.prev");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        Integer year = Integer.valueOf(path.getFileName().toString());
        path = path.getParent();
        DateTime time = new DateTime(year, 1, 1, 0, 0, 0, 0).minusYears(1);
        if (onlineTime > time.getMillis()) {
            return null;
        }
        return path.resolve(String.valueOf(time.getYear()));
    }
}
