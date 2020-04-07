package com.excenergy.tagdataserv;

import com.excenergy.protocol.TagValue;
import com.excenergy.tagdataserv.mem.TVMemItem;
import com.excenergy.tagdataserv.mem.TVMemPool;
import com.excenergy.tagdataserv.mem.TVMemSlot;
import com.excenergy.tagmeta.RealTag;
import com.excenergy.tagmeta.Tag;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.excenergy.tagdataserv.net.TagValueMapper.trans;

/**
 * God Bless You!
 * Author: Li Pengpeng
 * Date: 2013-11-25
 */
public class PreProcesser {
    private static final Logger logger = LoggerFactory.getLogger(PreProcesser.class);
    public static final int RATIO = 100;

    private final Application app;
    private final TagFactory tagFactory;

    private RealTag tag;
    private int handle;
    private int prHandle;
    private int phrHandle;
    private int pdrHandle;

    // 前面这么多值的平均值
    private double average = -1;

    // 前面值的个数
    private int count = 0;
    private int processInterval;
    private TagValue real;

    public PreProcesser(Application app, RealTag tag) {
        this.tag = tag;
        this.app = app;

        tagFactory = app.getTagFactory();
        this.handle = tag.getHandle();
        TagFactory tagFactory = app.getTagFactory();
        Tag prTag = tagFactory.get("p".concat(tag.getName()), false);
        Tag phrTag = tagFactory.get("ph".concat(tag.getName()), false);
        Tag pdrTag = tagFactory.get("pd".concat(tag.getName()), false);
        if (prTag == null || phrTag == null || pdrTag == null) {
            String code = "E-000013";
            String msg = String.format(TagDataException.getMsg(code), tag.getName());
            if (logger.isErrorEnabled()) {
                logger.error(msg);
            }
            throw new TagDataException(code, msg);
        }
        prHandle = prTag.getHandle();
        phrHandle = phrTag.getHandle();
        pdrHandle = pdrTag.getHandle();
        processInterval = tag.getPrInterval() * 1000;
        TV val = app.getMemPool().getVal(handle);
        if(val == null) {
            real = null;
        } else {
            real = trans(val, tag.getValType());
        }
    }

    /**
     * 生成PR并写入库。应当只在tv为实时值时调用。服务启动后，第1个值不会生成PR，历史补传数据不生成PR     *
     *
     * @param tagValue 实时值
     */
    public void process(TagValue tagValue) {
        if (!tagValue.isQualityGood()) {
            return;
        }

        if (real == null) {
            real = tagValue;
            return;
        }

        if (tagValue.getTime().getMillis() > real.getTime().getMillis()) { // is real time value
            if (!real.isQualityGood()) {
                real = tagValue;
                return;
            }

            TagValue increment = minus(tagValue, real);
            if (increment == null) {
                real = tagValue;
                return;
            }

            writeToMem(prHandle, trans(addIncValue(prHandle, increment.getTime(), increment)));
            writeToMem(phrHandle, trans(addIncValue(phrHandle, increment.getTime().millisOfSecond().withMinimumValue().secondOfMinute().withMinimumValue().minuteOfHour().withMinimumValue(), increment)));
            writeToMem(pdrHandle, trans(addIncValue(pdrHandle, increment.getTime().millisOfDay().withMinimumValue(), increment)));

            real = tagValue;
        } else { // is his report data
            long valTime = tagValue.getTime().getMillis();
            long curDv = valTime / processInterval;
            long curPrTime = curDv * processInterval;
            long prePrTime = curPrTime - processInterval;
            long nextPrTime = curPrTime + processInterval;
            long nNextPrTime = curPrTime + 2 * processInterval;
            NavigableMap<Long, TV> iterator = app.getMemPool().iterator(handle, prePrTime, nNextPrTime);
            if (iterator == null || iterator.isEmpty()) { // no value in this 3 pr interval
                return;
            }

            NavigableMap<Long, TagValue> iter = transTvMap(iterator);

            iter.put(valTime, tagValue);

            Long preFirst = iter.ceilingKey(prePrTime);
            if (preFirst >= curPrTime) {
                preFirst = null;
            }
            Long curFirst = iter.ceilingKey(curPrTime);
            Long nextFirst = iter.ceilingKey(nextPrTime);

            //  计算前一个周期的PR值
            if (preFirst != null && curFirst.equals(valTime)) {
                //  当前点减去前一个周期的第1个值，得到前一个周期的PR值
                writePr(accumulatePr(iter, preFirst, curFirst));
            }

            //   计算本周期的PR值
            if (nextFirst == null) {
                nextFirst = iter.lastKey();
                //  下一个周期的第一个值，减去当前值，得到本周期的PR值
            }

            if (curFirst < nextFirst) {
                writePr(accumulatePr(iter, curFirst, nextFirst));
            }
        }
    }

    private NavigableMap<Long, TagValue> transTvMap(NavigableMap<Long, TV> iterator) {
        NavigableMap<Long, TagValue> result = new TreeMap<>();
        for (Map.Entry<Long, TV> entry : iterator.entrySet()) {
            TagValue tv = trans(entry.getValue(), tag.getValType());
            if (tv.isQualityGood()) {
                result.put(entry.getKey(), tv);
            }
        }
        return result;
    }

    private TagValue accumulatePr(NavigableMap<Long, TagValue> iterator, Long preFirst, Long curFirst) {
        List<TagValue> prInterval = new ArrayList<>();
        for (Map.Entry<Long, TagValue> entry : iterator.entrySet()) {
            Long key = entry.getKey();
            TagValue value = entry.getValue();
            if (key < preFirst) {
                continue;
            }
            if (key >= curFirst) {
                break;
            }

            prInterval.add(minus(iterator.higherEntry(key).getValue(), value));
        }
        long preDv = preFirst / processInterval;
        return addAll(new DateTime((preDv + 1) * processInterval), prInterval);
    }

    private void writePr(TagValue pr) {
        write(prHandle, trans(pr));

        DateTime hourStart = pr.getTime().millisOfSecond().withMinimumValue().secondOfMinute().withMinimumValue().minuteOfHour().withMinimumValue();
        write(phrHandle, trans(addAll(hourStart, transTvMap(app.getMemPool().iterator(prHandle, hourStart.getMillis(), hourStart.plusHours(1).getMillis() - 1)).values())));

        DateTime dayStart = pr.getTime().millisOfDay().withMinimumValue();
        write(pdrHandle, trans(addAll(dayStart, transTvMap(app.getMemPool().iterator(phrHandle, dayStart.getMillis(), dayStart.plusDays(1).getMillis() - 1)).values())));
    }

    private TagValue addAll(DateTime time, Collection<TagValue> values) {
        double val = 0.0;
        for (TagValue value : values) {
            if (value == null) {
                continue;
            }
            val += value.getDoubleVal();
        }
        return new TagValue(time, tag.getValType(), val, TagValue.GOOD);
    }

    private TagValue minus(TagValue cur, TagValue pre) {
        //  根据时间，判断是否需要生成PR
        long curDv = cur.getTime().getMillis() / processInterval;
        long preDv = pre.getTime().getMillis() / processInterval;
        long dv = curDv - preDv;

        if (dv < 0) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("minusLatest error, handle:%d, curTime:%s, preTime:%s", handle, cur.getTime(), pre.getTime()));
            }
            return null;
        } else if (dv > 1) {
            //  为了解决采集器漏点问题，增加PR补点功能，如果漏了几个PR，则补插这几个点
            double averageValue = (cur.getDoubleVal() - pre.getDoubleVal()) / dv;
            //  判断值是否合法
            if (averageValue < 0 || (average > 0 && averageValue >= average * RATIO)) {
                String code = "DATA-000003";
                String msg = String.format(TagDataException.getMsg(code), handle, averageValue);
                if (logger.isWarnEnabled()) {
                    logger.warn(msg);
                }
                app.getMsgQueueService().sendMsg(new EtsdbMsg(tagFactory.getDevice(tag.getDeviceId()).getDesc(), EtsdbMsg.TYPE_VALUE_ERROR, String.format("average:%f,current:%f", average, averageValue), new Date()));
                return null;
            }
            for (long prDv = preDv + 1; prDv <= curDv; prDv++) {
                writePr(new TagValue(new DateTime(prDv * processInterval), cur.getValType(), averageValue, TagValue.BAD_VAL));
            }
            return null;
        } else {
            //  生成新的值，即减去上一条
            double incValue = cur.getDoubleVal() - pre.getDoubleVal();

            //  判断值是否合法
            if (incValue < 0 || (average > 0 && incValue >= average * RATIO)) {
                String code = "DATA-000003";
                String msg = String.format(TagDataException.getMsg(code), handle, incValue);
                if (logger.isWarnEnabled()) {
                    logger.warn(msg);
                }
                return null;
            }
            average = (average * count + incValue) / ++count;

            //  该值对应的PR值的时间
            long prTime = (preDv + 1) * processInterval;
            return new TagValue(new DateTime(prTime), tag.getValType(), incValue, TagValue.GOOD);
        }
    }

    private TagValue addIncValue(int handle, DateTime time, TagValue increment) {
        TV val = app.getMemPool().getVal(handle, time.getMillis());
        if (val == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("new pxr, handle:%d, time:%s", handle, time));
            }
            increment.setTime(time);
            return increment;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("add pxr, handle:%d, time:%s", handle, time));
            }
            TagValue pre = trans(val, tag.getValType());
            pre.setDouble(pre.getDoubleVal() + increment.getDoubleVal());
            return pre;
        }
    }

    private void write(int handle, TV tagValue) {
        if (app.getMemPool().isHis(handle, tagValue)) {
            writeToHis(handle, tagValue);
        } else {
            writeToMem(handle, tagValue);
        }
    }

    private void writeToMem(int handle, TV tagValue) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Write pr handle:%d, TagValue:%s", handle, tagValue));
        }
        TVMemItem item;
        synchronized (TVMemItem.class) {
            // 同步生成 Item， 并写日志缓存
            item = new TVMemItem(tagValue);
            if (tag.isPersist()) {
                app.getTvLog().append(handle, item);
            }
        }

        app.getMemPool().put(handle, item);
    }

    private void writeToHis(int handle, TV value) {
        Set<TV> set = new HashSet<>();
        set.add(value);

        try {
            app.getTvdb().put(handle, set);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Write disk IO Error, Tag:%s", tag), e);
            }
        }
    }
}
