/**
 *
 */
package com.excenergy.tagdataserv;

import com.excenergy.emq.MsgQueueService;
import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.disk.TVDB;
import com.excenergy.tagdataserv.log.TVLog;
import com.excenergy.tagdataserv.mem.TVMemItem;
import com.excenergy.tagdataserv.mem.TVMemPool;
import com.excenergy.tagdataserv.net.TagValueMapper;
import com.excenergy.tagmeta.RealTag;
import com.excenergy.tagmeta.Tag;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.excenergy.tagdataserv.TagDataException.getMsg;
import static com.excenergy.tagdataserv.Utils.timeFormat;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-08-21
 */
public class TagStore {
    private static final Logger logger = LoggerFactory.getLogger(TagStore.class);
    private PreProcesser preProcesser;
    private Application app;
    protected int handle;
    private TagFactory tagFactory;
    private TVMemPool memPool;
    private TVDB tvdb;
    private TVLog tvLog;
    private long onlineTime;
    private MsgQueueService msgQueueService;

    public TagStore(int handle, TagFactory tagFactory, TVMemPool memPool, TVDB tvdb, TVLog tvLog) {
        this.handle = handle;
        this.tagFactory = tagFactory;
        this.memPool = memPool;
        this.tvdb = tvdb;
        this.tvLog = tvLog;
    }

    public TagStore(Integer handle, Application app) {
        this(handle, app.getTagFactory(), app.getMemPool(), app.getTvdb(), app.getTvLog());
        this.app = app;
        msgQueueService = app.getMsgQueueService();
        onlineTime = app.getOnlineTime();
    }

    public void writeVal(TagValue tagValue) {
        if (tagValue == null) {
            String code = "E-000008";
            String msg = String.format(TagDataException.getMsg(code), "TagValue", "TagStore.writeVal");
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }

        tagValue.setValType(tagFactory.get(handle).getValType()); // ??????????????????????????????????????????????????????????????????

        write(tagValue);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     *
     * @param tagValue ?????????
     */
    public void write(TagValue tagValue) {
        TV tv = TagValueMapper.trans(tagValue);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("writeVal tagValue:%s, handle:%d", tagValue, handle));
        }
        long timestamp = tv.getTimestamp();
        if (timestamp < onlineTime || timestamp > System.currentTimeMillis() + 1000 * 60 * app.getConfig().getInt("application.time_tolerance")) {
            String code = "E-000001";
            String msg = String.format(getMsg(code), timeFormat(onlineTime), timeFormat(System.currentTimeMillis()), timeFormat(timestamp));
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }

        Tag tag = tagFactory.get(handle);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("writeVal tv:%s,handle:%d", tv.toString(tag.getValType()), handle));
        }
        // ??????????????????????????????
        if (tag.isReal()) { // ????????????????????????
            RealTag r = (RealTag) tag;
            int acc = r.getCompressAcc();
            if (r.isEnableCompress() && acc > 0) {// ???????????????????????????
                int compressAcc = acc * DateTimeConstants.MILLIS_PER_SECOND;
                long startTime = timestamp - timestamp % compressAcc;
                long endTime = startTime + compressAcc;
                NavigableMap<Long, TV> iterator = memPool.iterator(handle, startTime, endTime);
                if (iterator != null && iterator.size() > 0) { // ????????????????????????????????????????????????
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Execute compress, ignore TV:%s, handle:%d", tv.toString(tag.getValType()), handle));
                    }
                    return;
                }
            }
        }

        //  ???????????????
        if(!tagValue.isQualityGood()) {
            msgQueueService.sendMsg(new EtsdbMsg(tagFactory.getDevice(tag.getDeviceId()).getDesc(), EtsdbMsg.TYPE_QUALITY_ERROR, tag.getName() + ": " + tagValue.getQuality(), tagValue.getTime().toDate()));
        }

        //  ???????????????
        if("12".equalsIgnoreCase(tag.getCode())) {
            msgQueueService.sendMsg(new EtsdbMsg(tagFactory.getDevice(tag.getDeviceId()).getDesc(), EtsdbMsg.TYPE_POWER_OFF, tagValue.getVal().toString(), tagValue.getTime().toDate()));
        }

        // ????????????????????????????????????????????????
        if (tag.isReal() && tag.isCumulativeVal()) {
            if (preProcesser == null) {
                preProcesser = new PreProcesser(app, (RealTag) tag);
            }
            preProcesser.process(tagValue);
        }

        if (memPool.isHis(handle, tv)) {
            if (logger.isDebugEnabled()) {
                logger.debug("The tv is not in ");
            }
            if (tag.isPersist()) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Write to disk directly. handle:%d, TV:%s", handle, tv.toString(tag.getValType())));
                }
                addToHis(tv, tag);
            }
            return;
        }

        TVMemItem item;
        synchronized (TVMemItem.class) { // ???????????? Item??? ??????????????????
            item = new TVMemItem(tv);
            if (tag.isPersist()) {
                tvLog.append(handle, item);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Write to mem. tv:%s,handle:%d", tv.toString(tag.getValType()), handle);
        }
        memPool.put(handle, item);
    }

    private void addToHis(TV tv, Tag tag) {
        Set<TV> list = new HashSet<>(1);
        list.add(tv);
        try {
            tvdb.put(handle, list);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Write disk IO Error, Tag:%s", tag), e);
            }
        }
    }

    public TagValue readReal() {
        Tag tag = tagFactory.get(handle);
        TV val = memPool.getVal(handle);
        if (val == null) {
            return null;
        }
        return TagValueMapper.trans(val, tag.getValType());
    }

    public List<TagValue> readHis(long startTime, long endTime) {
        if (startTime > endTime) {
            long tmp = startTime;
            startTime = endTime;
            endTime = tmp;
        }

        long curTime = System.currentTimeMillis();
        if (startTime < onlineTime || startTime > curTime) {
            return null;
        }

        if (endTime > curTime) {
            endTime = curTime;
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("readHis, handle:%d, startTime:%d, endTime:%d", handle, startTime, endTime));
        }
        NavigableMap<Long, TV> map = memPool.iterator(handle, startTime, endTime);
        if (map == null || map.isEmpty()) {
            return null;
        }
        ArrayList<TagValue> result = new ArrayList<>(map.size());
        Tag tag = tagFactory.get(handle);
        for (Map.Entry<Long, TV> entry : map.entrySet()) {
            TV value = entry.getValue();
            if (value == null) {
                result.add(null);
            } else {
                result.add(TagValueMapper.trans(value, tag.getValType()));
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("readHis handle:%d, result:%s", handle, result));
        }
        return result;
    }

    /**
     * ?????????????????????????????????
     *
     * @param startTime ????????????
     * @param endTime   ????????????
     * @param interval  ???????????????????????????
     * @return ??????????????????
     */
    public List<TagValue> readHis(long startTime, long endTime, int interval) {
        if (startTime > endTime) {
            long tmp = startTime;
            startTime = endTime;
            endTime = tmp;
        }

        long curTime = System.currentTimeMillis();
        if (startTime < onlineTime) {
            String code = "E-000001";
            String msg = String.format(getMsg(code), timeFormat(onlineTime), timeFormat(curTime), timeFormat(startTime));
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }

        //  ??????????????????????????????????????????????????????????????????????????????????????????
//        if (endTime >= curTime) {
//            endTime = curTime;
//        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("readHis handle:%d, startTime:%d, endTime:%d, interval:%d", handle, startTime, endTime, interval));
        }
        Tag tag = tagFactory.get(handle);

        if (interval <= 0) {
            String code = "E-000010";
            String msg = String.format(TagDataException.getMsg(code), interval);
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            interval = tag.getTolerance();
        }

        byte mi = tag.getMergeInterval();
        int intvl = interval * 1000;
        if ((mi == Tag.HOURLY && interval > 3600) || (mi == Tag.DAILY && interval > 3600 * 24) || (mi == Tag.MONTHLY && interval > 3600 * 24 * 31)) {
            ArrayList<TagValue> result = new ArrayList<>((int) ((endTime - startTime) / interval + 1));
            for (long time = startTime; time <= endTime; time += intvl) {
                TagValue val = readHis(time, tag.getTolerance());
                if (val != null) {
                    val.setTime(new DateTime(time));
                }
                result.add(val);
            }
            return result;
        }

        NavigableMap<Long, TV> map = memPool.iterator(handle, startTime - intvl / 2, endTime + intvl / 2);

        ArrayList<TagValue> result = new ArrayList<>((int) ((endTime - startTime) / intvl));

        for (; startTime <= endTime; startTime += intvl) { //??????????????????????????????
            Map.Entry<Long, TV> entry = map.ceilingEntry(startTime);
            if (entry != null && entry.getKey() - startTime <= intvl / 2) {
                TagValue trans = TagValueMapper.trans(entry.getValue(), tag.getValType());
                trans.setTime(new DateTime(startTime));
                result.add(trans);
                continue;
            }

            entry = map.floorEntry(startTime);
            if (entry != null && startTime - entry.getKey() <= intvl / 2) {
                TagValue trans = TagValueMapper.trans(entry.getValue(), tag.getValType());
                trans.setTime(new DateTime(startTime));
                result.add(trans);
                continue;
            }

            if (startTime - curTime > intvl / 2) {
                //  ???????????????????????????????????????????????????
                result.add(new TagValue(new DateTime(startTime), tag.getValType(), -1.0, TagValue.NO_VAL));
            } else if (startTime - curTime < intvl / 2 && curTime - startTime < intvl / 2) {
                //  ????????????????????????????????????????????????????????????
                TagValue val = readReal();
                val.setTime(new DateTime(startTime));
                val.setQuality(TagValue.NO_VAL);
                result.add(val);
            } else {
                //  ????????????????????????????????????????????????????????????
                result.add(new TagValue(new DateTime(startTime), tag.getValType(), -1.0, TagValue.EMPTY));
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("readHis handle:%d, interval result:%s", handle, result));
        }

        return result;
    }

    public TagValue readHis(long time, int tolerance) {
        return readHis(time, time, tolerance).get(0);
    }

    public boolean clear(Integer handle) {
        try {
            tvdb.delete(handle, app.getOnlineTime(), System.currentTimeMillis());
        } catch (IOException e) {
            logger.error("clean his error.", e);
        }
        return memPool.clear(handle);
    }
}
