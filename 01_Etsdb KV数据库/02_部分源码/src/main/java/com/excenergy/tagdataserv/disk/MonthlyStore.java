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
public class MonthlyStore implements StoreStrategy {
    private static final Logger logger = LoggerFactory.getLogger(MonthlyStore.class);
    private final long onlineTime;

    public MonthlyStore(Long onlineTime) {
        this.onlineTime = onlineTime;
    }

    @Override
    public Path path(Path basePath, long timestamp) {
        if (basePath == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "basePath", "MonthlyStore.path");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get store path, basePath:%s, timestamp:%d", basePath.toString(), timestamp));
        }
        if (timestamp < onlineTime) {
            String code = "E-000001";
            String msg = String.format(getMsg(code), timeFormat(onlineTime), timeFormat(System.currentTimeMillis()), timeFormat(timestamp));
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        DateTime time = new DateTime(timestamp);
        Path resolve = basePath.resolve(String.valueOf(time.getYear())).resolve(String.valueOf(time.getMonthOfYear()));
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get store path, basePath:%s, timestamp:%d, store path:%s", basePath.toString(), timestamp, resolve.toString()));
        }
        return resolve;
    }

    @Override
    public Path next(Path path) {
        if (path == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "path", "MonthlyStore.next");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get next store path by path:%s", path.toString()));
        }
        Integer month = Integer.valueOf(path.getFileName().toString());
        path = path.getParent();
        Integer year = Integer.valueOf(path.getFileName().toString());
        path = path.getParent();
        DateTime time = new DateTime(year, month, 1, 0, 0, 0, 0).plusMonths(1);
        if (time.getMillis() > System.currentTimeMillis()) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Reach the current month, the next month is not exist. current time:%d", System.currentTimeMillis()));
            }
            return null;
        }
        Path result = path.resolve(String.valueOf(time.getYear())).resolve(String.valueOf(time.getMonthOfYear()));
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get next store path by path:%s, next path:%s", path.toString(), result.toString()));
        }
        return result;
    }

    @Override
    public Path prev(Path path) {
        if (path == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "path", "MonthlyStore.prev");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get prev store path by path:%s", path.toString()));
        }
        Integer month = Integer.valueOf(path.getFileName().toString());
        path = path.getParent();
        Integer year = Integer.valueOf(path.getFileName().toString());
        path = path.getParent();
        DateTime time = new DateTime(year, month, 1, 0, 0, 0, 0).minusMonths(1);
        if (onlineTime > time.getMillis()) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Reach the online month, the prev month is not exist. online time:%d current time:%d", onlineTime, System.currentTimeMillis()));
            }
            return null;
        }
        Path result = path.resolve(String.valueOf(time.getYear())).resolve(String.valueOf(time.getMonthOfYear()));
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Get prev store path by path:%s, prev path:%s", path.toString(), result.toString()));
        }
        return result;
    }
}
