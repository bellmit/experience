/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package etsdb.domains;

import com.alibaba.fastjson.annotation.JSONField;
import etsdb.util.DateTimePattern;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static etsdb.util.DateTimePattern.FORMATTER;
import static etsdb.util.DateTimePattern.TIME_PATTERN;

/**
 * @author michael
 */
public final class DataPoint {
    public static final int OK = 192;

    private String metric;
    private long timestamp;
    private String val;
    private int quality;

    public DataPoint() {
        // for JSON transform
    }

    public DataPoint(String metric, long timestamp, String val) {
        this(metric, timestamp, val, OK);
    }

    public DataPoint(String metric, Instant timestamp, String val) {
        this(metric, timestamp.toEpochMilli(), val, OK);
    }

    public DataPoint(String metric, Instant timestamp, String val, int quality) {
        this(metric, timestamp.toEpochMilli(), val, quality);
    }

    public DataPoint(String metric, LocalDateTime dateTime, String val, int quality) {
        this(metric, Timestamp.valueOf(dateTime).getTime(), val, quality);
    }

    public DataPoint(String metric, long timestamp, String val, int quality) {
        this.metric = metric;
        this.timestamp = timestamp - timestamp % 1000;
        this.val = val;
        this.quality = quality;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getTime() {
        return new Timestamp(timestamp).toLocalDateTime().format(FORMATTER);
    }

    public void setTime(String time) {
        if (time == null || !time.matches(TIME_PATTERN)) {
            throw new RuntimeException(String.format("time formatter error, must: %s, now: %s", DateTimePattern.TIME_FORMAT, time));
        }
        setTimestamp(Timestamp.valueOf(time).getTime());
    }

    @JSONField(deserialize = false)
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp - timestamp % 1000;
    }

    @JSONField(serialize = false)
    public long getTimestamp() {
        return this.timestamp;
    }

    @JSONField(serialize = false)
    public Date getDate() {
        return new Date(timestamp);
    }

    @JSONField(serialize = false)
    public LocalDateTime getDateTime() {
        return new Timestamp(this.timestamp).toLocalDateTime();
    }

    @JSONField(deserialize = false)
    public void setDateTime(LocalDateTime dateTime) {
        setTimestamp(Timestamp.valueOf(dateTime).getTime());
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public void setVal(Object val) {
        this.val = String.valueOf(val);
    }

    public int getQuality() {
        return quality;
    }

    public Quality quality() {
        return Quality.valueOf(quality);
    }

    @JSONField(deserialize = false)
    public void setQuality(Quality quality) {
        this.quality = quality.val;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    @JSONField(deserialize = false)
    public void setQuality(String quality) {
        this.quality = Quality.valueOf(quality).val;
    }

    public boolean valid() {
        return this.quality == OK;
    }

    public String val() {
        return this.val;
    }

    // 1: true, otherwise: false
    public boolean boolVal() {
        return "1".equals(this.val);
    }

    public int intVal() {
        return Integer.parseInt(val);
    }

    public boolean isDouble() {
        try {
            Double.parseDouble(val);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public double doubleVal() {
        return Double.parseDouble(val);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DataPoint dataPoint = (DataPoint) o;

        if (timestamp != dataPoint.timestamp)
            return false;
        if (quality != dataPoint.quality)
            return false;
        if (metric != null ? !metric.equals(dataPoint.metric) : dataPoint.metric != null)
            return false;
        return val != null ? val.equals(dataPoint.val) : dataPoint.val == null;

    }

    @Override
    public int hashCode() {
        int result = metric != null ? metric.hashCode() : 0;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (val != null ? val.hashCode() : 0);
        result = 31 * result + quality;
        return result;
    }

    @Override
    public String toString() {
        return "DataPoint{" + "metric='" + metric + '\'' + ", timestamp='" + getTime() + '\''
                + ", val='" + val + '\'' + ", quality=" + quality + '}';
    }

    public boolean checkValid() {
        return metric != null && !metric.isEmpty() && val != null && !val.isEmpty() && timestamp >= 0
                && quality > 0;
    }

    public enum Quality {
        // collector define
        POINT_UN_INIT(10), POINT_INIT_FAIL(14), POINT_INIT_SUCCESS(15), METER_NO_RESPONSE(16), //
        METER_RESPONSE_LESS(17), METER_RESPONSE_CHECK_FAIL(18), DATA_FRAME_FORMAT_ERROR(32), //
        METER_RESPONSE_ERROR_CODE(33), COLLECTOR_UNSUPPORTED(48), PACKING_ERROR(49), //
        // web define
        ERROR(100), IGNORE(111), REVERSED(119), EMPTY(120), //
        // collector control
        CTRL(160), DRIVER_UNCONNECTED_RTDB(162), DRIVER_UNCONNECTED_EDC(164), //
        EDC_LOCATE_POINT_FAIL(166), COLLECTOR_OFFLINE(167), WAITING_COLLECTOR(168), COLLECTOR_FAIL(169), //
        // other
        BAD_VAL(170), NO_VAL(171), GOOD(192);

        private static final List<Quality> ALL = Arrays.asList(POINT_UN_INIT, POINT_INIT_FAIL, //
                POINT_INIT_SUCCESS, METER_NO_RESPONSE,//
                METER_RESPONSE_LESS, METER_RESPONSE_CHECK_FAIL, DATA_FRAME_FORMAT_ERROR, //
                METER_RESPONSE_ERROR_CODE, COLLECTOR_UNSUPPORTED, PACKING_ERROR,//
                ERROR, IGNORE, REVERSED, EMPTY, //
                CTRL, DRIVER_UNCONNECTED_RTDB, DRIVER_UNCONNECTED_EDC, //
                EDC_LOCATE_POINT_FAIL, COLLECTOR_OFFLINE, WAITING_COLLECTOR, COLLECTOR_FAIL,//
                BAD_VAL, NO_VAL, GOOD);

        private final int val;

        Quality(int val) {
            this.val = val;
        }

        public int getVal() {
            return val;
        }

        public static Quality valueOf(int val) {
            Optional<Quality> first = ALL.stream().filter(e -> e.val == val).findFirst();
            if (first.isPresent()) {
                return first.get();
            } else {
                throw new RuntimeException("Error quality: " + val);
            }
        }

        @Override
        public String toString() {
            return this.name();
        }
    }
}
