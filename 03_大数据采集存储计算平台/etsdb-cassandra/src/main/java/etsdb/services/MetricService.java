/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package etsdb.services;

import com.google.common.collect.Lists;
import etsdb.domains.DataPoint;
import etsdb.util.DateTimePattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static etsdb.util.DateTimePattern.*;
import static etsdb.util.DateUtils.*;
import static java.time.LocalDateTime.of;
import static java.time.LocalDateTime.parse;

/**
 * 接口分为3类： 1. 写，分为写单个值1.1 和 批量写1.2，共2个接口 2. 读，分为读单点2.1 和 读一段时间2.2 3. 采样，分为采实时值3.1，单点采样3.2 和 间隔采样3.3
 */
public interface MetricService {
    String MINUTELY_SUFFIX = ".minutely";
    String MINUTE5LY_SUFFIX = ".5minutely";
    String MINUTE10LY_SUFFIX = ".10minutely";
    String MINUTE15LY_SUFFIX = ".15minutely";
    String MINUTE20LY_SUFFIX = ".20minutely";
    String MINUTE30LY_SUFFIX = ".30minutely";
    String HOURLY_SUFFIX = ".hourly";
    String DAILY_SUFFIX = ".daily";
    String MONTHLY_SUFFIX = ".monthly";
    String YEARLY_SUFFIX = ".yearly";

    static boolean isHourly(String metric) {
        return metric.endsWith(HOURLY_SUFFIX);
    }

    static boolean isDaily(String metric) {
        return metric.endsWith(DAILY_SUFFIX);
    }

    static boolean isMonthly(String metric) {
        return metric.endsWith(MONTHLY_SUFFIX);
    }

    static boolean isYearly(String metric) {
        return metric.endsWith(YEARLY_SUFFIX);
    }

    /**
     * 1.1 write single dp
     *
     * @param dp dp
     */
    void write(DataPoint dp);

    /**
     * 1.2 write dps
     *
     * @param dps dps
     */
    default void write(List<DataPoint> dps) {
        checkArgument(Objects.nonNull(dps));

        dps.forEach(this::write);
    }

    default CompletableFuture<Void> writeAsync(DataPoint dp) {
        return CompletableFuture.runAsync(() -> write(dp));
    }

    /**
     * 2.1.1 read fixed time
     *
     * @param metric    metric
     * @param timestamp timestamp
     * @return the value or null
     */
    @Nullable
    default DataPoint read(String metric, String timestamp) {
        return read(metric, timestamp, true);
    }

    /**
     * 2.1.2 read fixed time
     *
     * @param metric    metric
     * @param timestamp timestamp
     * @return the value or null
     */
    @Nullable
    default DataPoint read(String metric, long timestamp) {
        return read(metric, timestamp, true);
    }

    /**
     * 2.1.3 read fixed time
     *
     * @param metric metric
     * @param ldt    ldt
     * @return the value or null
     */
    @Nullable
    default DataPoint read(String metric, LocalDateTime ldt) {
        return read(metric, Timestamp.valueOf(ldt).getTime(), true);
    }

    /**
     * 2.1.4 read fixed time
     *
     * @param metric    metric
     * @param time      format yyyy-MM-dd hh:mm:ss
     * @param onlyValid valid or invalid
     * @return the value or null
     */
    @Nullable
    default DataPoint read(String metric, String time, boolean onlyValid) {
        checkArgument(time.matches(TIME_PATTERN));
        Timestamp t = Timestamp.valueOf(time);
        return read(metric, t.getTime(), onlyValid);
    }

    /**
     * 2.1.5 read fixed time
     *
     * @param metric    metric
     * @param ldt       ldt
     * @param onlyValid valid or invalid
     * @return the value or null
     */
    @Nullable
    default DataPoint read(String metric, LocalDateTime ldt, boolean onlyValid) {
        Timestamp t = Timestamp.valueOf(ldt);
        return read(metric, t.getTime(), onlyValid);
    }

    /**
     * 2.1.6 read fixed time
     *
     * @param metric    metric
     * @param timestamp timestamp
     * @param onlyValid valid or invalid
     * @return the value or null
     */
    @Nullable
    default DataPoint read(String metric, long timestamp, boolean onlyValid) {
        List<DataPoint> r = read(metric, timestamp, timestamp, onlyValid);
        return r.isEmpty() ? null : r.get(0);
    }

    /**
     * 2.2.1 read data from start to end
     *
     * @param metric metric
     * @param start  start
     * @param end    end
     * @return dataPoint list
     */
    @NotNull
    default List<DataPoint> read(String metric, long start, long end) {
        return read(metric, start, end, true);
    }

    /**
     * 2.2.2 read data from start to end
     *
     * @param metric metric
     * @param start  start
     * @param end    end
     * @return dataPoint list
     */
    @NotNull
    default List<DataPoint> read(String metric, String start, String end) {
        return read(metric, start, end, true);
    }

    /**
     * 2.2.3 read data from start to end
     *
     * @param metric    metric
     * @param start     start
     * @param end       end
     * @param onlyValid onlyValid
     * @return dataPoint list
     */
    @NotNull
    default List<DataPoint> read(String metric, LocalDateTime start, LocalDateTime end,
                                 boolean onlyValid) {
        Timestamp s = Timestamp.valueOf(start);
        Timestamp e = Timestamp.valueOf(end);
        return this.read(metric, s.getTime(), e.getTime(), onlyValid);
    }

    /**
     * 2.2.4 read data from start to end
     *
     * @param metric    metric
     * @param start     start
     * @param end       end
     * @param onlyValid onlyValid
     * @return dataPoint list
     */
    @NotNull
    default List<DataPoint> read(String metric, String start, String end, boolean onlyValid) {
        checkArgument(start.matches(TIME_PATTERN));
        checkArgument(end.matches(TIME_PATTERN));

        Timestamp s = Timestamp.valueOf(start);
        Timestamp e = Timestamp.valueOf(end);
        return this.read(metric, s.getTime(), e.getTime(), onlyValid);
    }

    /**
     * 2.2.4 read data from start to end
     *
     * @param metric    metric
     * @param start     start
     * @param end       end
     * @param onlyValid onlyValid
     * @return dataPoint list
     */
    @NotNull
    List<DataPoint> read(String metric, long start, long end, boolean onlyValid);

    default OptionalDouble readDouble(String metric, LocalDateTime ldt) {
        DataPoint dp = read(metric, ldt);
        if (dp != null && dp.isDouble()) {
            return OptionalDouble.of(dp.doubleVal());
        } else {
            return OptionalDouble.empty();
        }
    }

    default OptionalDouble average(String metric, LocalDateTime begin, LocalDateTime end) {
        List<DataPoint> dps = read(metric, begin, end, true);
        if (!dps.isEmpty()) {
            return dps.stream().filter(dp -> dp != null && dp.isDouble()).mapToDouble(DataPoint::doubleVal).average();
        } else {
            return OptionalDouble.empty();
        }
    }

    default OptionalDouble min(String metric, LocalDateTime begin, LocalDateTime end) {
        List<DataPoint> dps = read(metric, begin, end, true);
        if (!dps.isEmpty()) {
            return dps.stream().filter(dp -> dp != null && dp.isDouble()).mapToDouble(DataPoint::doubleVal).min();
        } else {
            return OptionalDouble.empty();
        }
    }

    default OptionalDouble max(String metric, LocalDateTime begin, LocalDateTime end) {
        List<DataPoint> dps = read(metric, begin, end, true);
        if (!dps.isEmpty()) {
            return dps.stream().filter(dp -> dp != null && dp.isDouble()).mapToDouble(DataPoint::doubleVal).max();
        } else {
            return OptionalDouble.empty();
        }
    }

    /**
     * 3.1.1 sample latest value
     *
     * @param metric metric
     * @return the value or null
     */
    @Nullable
    default DataPoint sample(String metric) {
        return sample(metric, true);
    }

    /**
     * 3.1.2 sample latest value
     *
     * @param metric metric
     * @param valid  valid
     * @return the value or null
     */
    @Nullable
    default DataPoint sample(String metric, boolean valid) {
        return sample(metric, System.currentTimeMillis(), valid);
    }

    /**
     * 3.2.1 sample timestamp value
     *
     * @param metric    metric
     * @param timestamp timestamp
     * @return the value or null
     */
    @Nullable
    default DataPoint sample(String metric, long timestamp) {
        return sample(metric, timestamp, true);
    }

    /**
     * 3.2.2 sample timestamp value
     *
     * @param metric metric
     * @param time   format 'yyyy-MM-dd HH:mm:ss'
     * @return the value or null
     */
    @Nullable
    default DataPoint sample(String metric, String time) {
        checkArgument(time.matches(TIME_PATTERN));
        return sample(metric, time, true);
    }

    /**
     * 3.2.3 sample timestamp value
     *
     * @param metric metric
     * @param ldt    ldt
     * @return the value or null
     */
    @Nullable
    default DataPoint sample(String metric, LocalDateTime ldt) {
        return sample(metric, Timestamp.valueOf(ldt).getTime(), true);
    }

    /**
     * 3.2.4 sample timestamp value
     *
     * @param metric metric
     * @param time   format 'yyyy-MM-dd HH:mm:ss'
     * @param valid  valid
     * @return the value or null
     */
    @Nullable
    default DataPoint sample(String metric, String time, boolean valid) {
        checkArgument(time.matches(TIME_PATTERN));
        return this.sample(metric, Timestamp.valueOf(time).getTime(), valid);
    }

    /**
     * 3.2.5 sample timestamp value
     *
     * @param metric metric
     * @param ldt    ldt
     * @param valid  valid
     * @return the value or null
     */
    @Nullable
    default DataPoint sample(String metric, LocalDateTime ldt, boolean valid) {
        return this.sample(metric, Timestamp.valueOf(ldt).getTime(), valid);
    }

    /**
     * 3.2.6 sample timestamp value
     *
     * @param metric    metric
     * @param timestamp timestamp
     * @param valid     valid
     * @return the value or null
     */
    @Nullable
    default DataPoint sample(String metric, long timestamp, boolean valid) {
        return this.sample(metric, timestamp, valid, false);
    }

    /**
     * 3.2.7 sample timestamp value
     *
     * @param metric    metric
     * @param timestamp timestamp
     * @param valid     valid
     * @return the value or null
     */
    @Nullable
    default DataPoint sample(String metric, LocalDateTime timestamp, boolean valid, boolean forward) {
        return this.sample(metric, Timestamp.valueOf(timestamp).getTime(), valid, forward);
    }

    /**
     * 3.2.8 sample timestamp value
     *
     * @param metric    metric
     * @param timestamp timestamp
     * @param valid     valid
     * @return the value or null
     */
    @Nullable
    DataPoint sample(String metric, long timestamp, boolean valid, boolean forward);

    /**
     * 3.3.1 sample from start to end
     *
     * @param metric   metric
     * @param start    start format 'yyyy-MM-dd HH:mm:ss'
     * @param end      end format 'yyyy-MM-dd HH:mm:ss'
     * @param interval interval
     * @return the value list
     */
    @NotNull
    default List<DataPoint> sample(String metric, String start, String end, int interval) {
        checkArgument(start.matches(TIME_PATTERN));
        checkArgument(end.matches(TIME_PATTERN));
        return sample(metric, Timestamp.valueOf(start).getTime(), Timestamp.valueOf(end).getTime(),
                interval);
    }

    /**
     * 3.3.2 sample from start to end
     *
     * @param metric   metric
     * @param start    start
     * @param end      end
     * @param interval interval
     * @return the value list
     */
    @NotNull
    default List<DataPoint> sample(String metric, LocalDateTime start, LocalDateTime end, int interval) {
        return sample(metric, Timestamp.valueOf(start).getTime(), Timestamp.valueOf(end).getTime(),
                interval);
    }

    /**
     * 3.3.3 sample from start to end
     *
     * @param metric   metric
     * @param start    start
     * @param end      end
     * @param interval interval unit: s
     * @return the value list
     */
    @NotNull
    default List<DataPoint> sample(String metric, long start, long end, int interval) {
        int msInterval = interval * 1000;
        List<DataPoint> r = read(metric, start - msInterval + 1, end, true);

        Map<Long, DataPoint> aMap =
                r.stream().collect(Collectors.toMap(DataPoint::getTimestamp, Function.identity()));
        ConcurrentSkipListMap<Long, DataPoint> map = new ConcurrentSkipListMap<>(aMap);

        List<Long> timeList = Lists.newArrayList();
        for (long time = start; time <= end; time += msInterval) {
            timeList.add(time);
        }

        return timeList.stream().map(time -> {
            Map.Entry<Long, DataPoint> entry = map.floorEntry(time);
            if (entry == null) {
                return null;
            } else {
                DataPoint item = entry.getValue();
                item.setTimestamp(time);
                return item;
            }
        }).collect(Collectors.toList());
    }

    default void delete(String metric, String begin, String end) {
        checkArgument(begin.matches(TIME_PATTERN));
        checkArgument(end.matches(TIME_PATTERN));
        delete(metric, Timestamp.valueOf(begin).getTime(), Timestamp.valueOf(end).getTime());
    }

    default void delete(String metric, LocalDateTime begin, LocalDateTime end) {
        delete(metric, Timestamp.valueOf(begin).getTime(), Timestamp.valueOf(end).getTime());
    }

    void delete(String metric, long begin, long end);

    static String metric(String metric) {
        if (metric.endsWith(HOURLY_SUFFIX)) {
            return metric.substring(0, metric.length() - HOURLY_SUFFIX.length());
        }
        if (metric.endsWith(DAILY_SUFFIX)) {
            return metric.substring(0, metric.length() - DAILY_SUFFIX.length());
        }
        if (metric.endsWith(MONTHLY_SUFFIX)) {
            return metric.substring(0, metric.length() - MONTHLY_SUFFIX.length());
        }
        if (metric.endsWith(YEARLY_SUFFIX)) {
            return metric.substring(0, metric.length() - YEARLY_SUFFIX.length());
        }
        return metric;
    }

    static String hourlyMetric(String metric) {
        return metric(metric) + HOURLY_SUFFIX;
    }

    static String dailyMetric(String metric) {
        return metric(metric) + DAILY_SUFFIX;
    }

    static String monthlyMetric(String metric) {
        return metric(metric) + MONTHLY_SUFFIX;
    }

    static String yearlyMetric(String metric) {
        return metric(metric) + YEARLY_SUFFIX;
    }

    default double hourTotal(String metric, LocalDateTime hour) {
        DataPoint p =
                this.read(hourlyMetric(metric), startOfHour(hour), true);

        if (p != null) {
            return p.doubleVal();
        } else {
            return 0.0;
        }
    }

    /**
     * @param hour format 'yyyy-MM-dd HH:00:00'
     */
    default double hourTotal(String metric, String hour) {
        checkArgument(hour.matches(HOUR_PATTERN));

        return hourTotal(metric, parse(hour, DateTimePattern.FORMATTER));
    }

    /**
     * @param start format 'yyyy-MM-dd HH:00:00'
     * @param end   format 'yyyy-MM-dd HH:00:00'
     */
    default double hourTotal(String metric, String start, String end) {
        checkArgument(start.matches(HOUR_PATTERN));
        checkArgument(end.matches(HOUR_PATTERN));

        return hourTotal(metric, parse(start, DateTimePattern.FORMATTER),
                parse(end, DateTimePattern.FORMATTER));
    }

    default double hourTotal(String metric, LocalDateTime start, LocalDateTime end) {
        List<DataPoint> dps = read(hourlyMetric(metric), start, end, true);
        return dps.stream().mapToDouble(DataPoint::doubleVal).sum();
    }

    default double dayTotal(String metric, LocalDate day) {
        DataPoint p = this.read(dailyMetric(metric), LocalDateTime.of(day, LocalTime.MIN), true);

        if (p != null) {
            return p.doubleVal();
        } else {
            return 0.0;
        }
    }

    /**
     * @param day format 'yyyy-MM-dd'
     */
    default double dayTotal(String metric, String day) {
        checkArgument(day.matches(DAY_PATTERN));

        return dayTotal(metric, LocalDate.parse(day));
    }

    /**
     * @param start format 'yyyy-MM-dd'
     * @param end   format 'yyyy-MM-dd'
     */
    default double dayTotal(String metric, String start, String end) {
        checkArgument(start.matches(DAY_PATTERN));
        checkArgument(end.matches(DAY_PATTERN));

        LocalDate s = LocalDate.parse(start);
        LocalDate e = LocalDate.parse(end);
        return dayTotal(metric, s, e);
    }

    default double dayTotal(String metric, LocalDate start, LocalDate end) {
        List<DataPoint> dps =
                read(dailyMetric(metric), start.atStartOfDay(), end.atStartOfDay(), true);
        return dps.stream().mapToDouble(DataPoint::doubleVal).sum();
    }

    /**
     * @param month format 'yyyy-MM'
     */
    default double monthTotal(String metric, String month) {
        checkArgument(month.matches(MONTH_PATTERN));

        return monthTotal(metric, YearMonth.parse(month));
    }

    default double monthTotal(String metric, YearMonth month) {
        LocalDateTime firstDay = of(month.atDay(1), LocalTime.MIN);
        DataPoint monthTotal = read(monthlyMetric(metric), firstDay, true);
        if (monthTotal != null) {
            return monthTotal.doubleVal();
        } else {
            LocalDateTime lastDay = of(month.atEndOfMonth(), LocalTime.MIN);

            List<DataPoint> dps = this.read(dailyMetric(metric), firstDay, lastDay, true);

            return dps.stream().mapToDouble(DataPoint::doubleVal).sum();
        }
    }

    /**
     * @param start format 'yyyy-MM'
     * @param end   format 'yyyy-MM'
     */
    default double monthTotal(String metric, String start, String end) {
        checkArgument(start.matches(MONTH_PATTERN));
        checkArgument(end.matches(MONTH_PATTERN));

        YearMonth s = YearMonth.parse(start);
        YearMonth e = YearMonth.parse(end);
        return monthTotal(metric, s, e);
    }

    default double monthTotal(String metric, YearMonth startMonth, YearMonth endMonth) {
        return dayTotal(metric, startMonth.atDay(1), endMonth.atEndOfMonth());
    }

    /**
     * @param year format 'yyyy'
     */
    default double yearTotal(String metric, String year) {
        checkArgument(year.matches(YEAR_PATTERN));

        return yearTotal(metric, Year.parse(year));
    }

    default double yearTotal(String metric, Year year) {
        LocalDateTime firstDay = of(year.atDay(1), LocalTime.MIN);
        DataPoint yearTotal = read(yearlyMetric(metric), firstDay, true);
        if (yearTotal != null) {
            return yearTotal.doubleVal();
        } else {
            LocalDateTime lastDay = of(year.atMonth(12).atEndOfMonth(), LocalTime.MIN);

            List<DataPoint> dps = this.read(dailyMetric(metric), firstDay, lastDay, true);

            return dps.stream().mapToDouble(DataPoint::doubleVal).sum();
        }
    }

    /**
     * @param start format 'yyyy-MM-dd HH:mm:ss'
     * @param end   format 'yyyy-MM-dd HH:mm:ss'
     */
    default double total(String metric, String start, String end) {
        checkArgument(start.matches(TIME_PATTERN));
        checkArgument(end.matches(TIME_PATTERN));

        return total(metric, parse(start, DateTimePattern.FORMATTER),
                parse(end, DateTimePattern.FORMATTER));
    }

    default double total(String metric, LocalDateTime start, LocalDateTime end) {
        if (start.plusHours(2).isAfter(end)) {
            // < 2h
            return minusTotal(metric, start, end);
        } else if (start.plusDays(2).isAfter(end)) {
            // [2h ~ 2d)
            return minusHourTotal(metric, start, end);
        } else {
            // [2d ~ ...)
            return minusHourDayTotal(metric, start, end);
        }
    }

    default double minusTotal(String metric, LocalDateTime start, LocalDateTime end) {
        if (start.plusHours(2).isBefore(end)) {
            throw new RuntimeException(
                    "nearby total: end time - start time > 2 hours, please use 'total' method instead.");
        }
        DataPoint startDp = sample(metric, start);
        DataPoint endDp = sample(metric, end);
        if (startDp != null && endDp != null) {
            return endDp.doubleVal() - startDp.doubleVal();
        } else {
            return 0;
        }
    }

    default double minusHourTotal(String metric, LocalDateTime start, LocalDateTime end) {
        LocalDateTime hourStart = start.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime hourEnd = end.minusHours(1).withMinute(0).withSecond(0).withNano(0);
        if (hourEnd.isBefore(hourStart)) {
            throw new RuntimeException("minusHourTotal: end - start must >= 2 hours.");
        }
        if (isWholeHour(start)) {
            if (isWholeHour(end)) {
                return hourTotal(metric, start, hourEnd);
            } else {
                return hourTotal(metric, start, hourEnd) + minusTotal(metric, hourEnd.plusHours(1), end);
            }
        } else {
            if (isWholeHour(end)) {
                return hourTotal(metric, hourStart, hourEnd) + minusTotal(metric, start, hourStart);
            } else {
                return hourTotal(metric, hourStart, hourEnd) + minusTotal(metric, start, hourStart)
                        + minusTotal(metric, hourEnd.plusHours(1), end);
            }
        }
    }

    default double minusHourDayTotal(String metric, LocalDateTime start, LocalDateTime end) {
        LocalDate dayStart = start.toLocalDate().plusDays(1);
        LocalDate dayEnd = end.toLocalDate().minusDays(1);
        if (dayEnd.isBefore(dayStart)) {
            throw new RuntimeException("minusHourDayTotal: end - start must >= 2 days.");
        }
        if (isWholeDay(start)) {
            if (isWholeDay(end)) {
                return dayTotal(metric, start.toLocalDate(), dayEnd);
            } else {
                return dayTotal(metric, start.toLocalDate(), dayEnd)
                        + minusHourTotal(metric, end.toLocalDate().atStartOfDay(), end);
            }
        } else {
            if (isWholeDay(end)) {
                return dayTotal(metric, dayStart, dayEnd)
                        + minusHourTotal(metric, start, dayStart.atStartOfDay());
            } else {
                return dayTotal(metric, dayStart, dayEnd)
                        + minusHourTotal(metric, start, dayStart.atStartOfDay())
                        + minusHourTotal(metric, end.toLocalDate().atStartOfDay(), end);
            }
        }
    }

    /**
     * @param start 'yyyy-MM-dd HH:00:00'
     * @param end   'yyyy-MM-dd HH:00:00'
     */
    default List<Double> sampleHourly(String metric, String start, String end) {
        checkArgument(start.matches(HOUR_PATTERN));
        checkArgument(end.matches(HOUR_PATTERN));
        return sampleHourly(metric, parse(start, DateTimePattern.FORMATTER),
                parse(end, DateTimePattern.FORMATTER));
    }

    default List<Double> sampleHourly(String metric, LocalDateTime start, LocalDateTime end) {
        LocalDateTime s = start.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime e = end.withMinute(0).withSecond(0).withNano(0);

        List<DataPoint> r = this.read(hourlyMetric(metric), s, e, true);
        Map<LocalDateTime, DataPoint> hourlyMap =
                r.stream().collect(Collectors.toMap(DataPoint::getDateTime, Function.identity()));

        List<LocalDateTime> hourList = Lists.newArrayList();
        for (LocalDateTime ldt = s; ldt.isBefore(e) || ldt.isEqual(e); ldt = ldt.plusHours(1)) {
            hourList.add(ldt);
        }

        return hourList.stream().map(hour -> {
            DataPoint dp = hourlyMap.get(hour);
            if (dp != null) {
                return dp.doubleVal();
            } else {
                return 0.0;
            }
        }).collect(Collectors.toList());
    }

    /**
     * @param day 'yyyy-MM-dd'
     */
    default List<Double> sampleHourly(String metric, String day) {
        checkArgument(day.matches(DAY_PATTERN));

        return this.sampleHourly(metric, LocalDate.parse(day));
    }

    default List<Double> sampleHourly(String metric, LocalDate day) {
        return sampleHourly(metric, of(day, LocalTime.MIN), of(day, LocalTime.MAX));
    }

    /**
     * @param start 'yyyy-MM-dd'
     * @param end   'yyyy-MM-dd'
     */
    default List<Double> sampleDaily(String metric, String start, String end) {
        checkArgument(start.matches(DAY_PATTERN));
        checkArgument(end.matches(DAY_PATTERN));
        return sampleDaily(metric, LocalDate.parse(start), LocalDate.parse(end));
    }

    default List<Double> sampleDaily(String metric, LocalDate start, LocalDate end) {
        LocalDateTime s = of(start, LocalTime.MIN);
        LocalDateTime e = of(end, LocalTime.MIN);

        List<DataPoint> r = this.read(dailyMetric(metric), s, e, true);
        Map<LocalDateTime, DataPoint> dailyMap =
                r.stream().collect(Collectors.toMap(DataPoint::getDateTime, Function.identity()));

        List<LocalDateTime> dayList = Lists.newArrayList();
        for (LocalDateTime ldt = s; ldt.isBefore(e) || ldt.isEqual(e); ldt = ldt.plusDays(1)) {
            dayList.add(ldt);
        }

        return dayList.stream().map(day -> {
            DataPoint dp = dailyMap.get(day);
            if (dp != null) {
                return dp.doubleVal();
            } else {
                return 0.0;
            }
        }).collect(Collectors.toList());
    }

    /**
     * @param month 'yyyy-MM'
     */
    default List<Double> sampleDaily(String metric, String month) {
        checkArgument(month.matches(MONTH_PATTERN));

        return sampleDaily(metric, YearMonth.parse(month));
    }

    default List<Double> sampleDaily(String metric, YearMonth month) {
        return sampleDaily(metric, month.atDay(1), month.atEndOfMonth());
    }

    /**
     * @param start 'yyyy-MM'
     * @param end   'yyyy-MM'
     */
    default List<Double> sampleMonthly(String metric, String start, String end) {
        checkArgument(start.matches(MONTH_PATTERN));
        checkArgument(end.matches(MONTH_PATTERN));
        return sampleMonthly(metric, YearMonth.parse(start), YearMonth.parse(end));
    }

    default List<Double> sampleMonthly(String metric, YearMonth start, YearMonth end) {
        ArrayList<YearMonth> monthList = Lists.newArrayList();
        for (YearMonth month = start; month.isBefore(end) || month.equals(end); month =
                month.plusMonths(1)) {
            monthList.add(month);
        }

        if (metric.startsWith("r.")) {
            List<DataPoint> daily = read(dailyMetric(metric), of(start.atDay(1), LocalTime.MIN), of(end.atEndOfMonth(), LocalTime.MIN), true);
            Map<YearMonth, List<DataPoint>> yearMonthListMap = daily.stream().collect(Collectors.groupingBy(dp -> YearMonth.from(dp.getDateTime())));
            Map<YearMonth, Double> doubleMap = yearMonthListMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().mapToDouble(DataPoint::doubleVal).sum()));

            return monthList.stream().map(month -> doubleMap.getOrDefault(month, 0.0)).collect(Collectors.toList());
        } else if (metric.startsWith("v.")) {
            List<DataPoint> monthly = read(monthlyMetric(metric), of(start.atDay(1), LocalTime.MIN), of(end.atDay(1), LocalTime.MIN), true);
            Map<LocalDateTime, Double> map = monthly.stream().collect(Collectors.toMap(DataPoint::getDateTime, DataPoint::doubleVal));
            return monthList.stream().map(month -> map.getOrDefault(month.atDay(1).atStartOfDay(), 0.0)).collect(Collectors.toList());
        } else {
            throw new RuntimeException("not support.");
        }
    }

    /**
     * @param year yyyy
     */
    default List<Double> sampleMonthly(String metric, String year) {
        checkArgument(year.matches(YEAR_PATTERN));

        return this.sampleMonthly(metric, Year.parse(year));
    }

    default List<Double> sampleMonthly(String metric, Year year) {
        return this.sampleMonthly(metric, year.atMonth(1), year.atMonth(12));
    }

    @Nullable
    default DataPoint calcRealMetricHour(String metric, LocalDateTime hour) {
        DataPoint thisHour = sample(metric, hour);
        DataPoint nextHour = sample(metric, hour.plusHours(1));
        if (thisHour != null //
                && nextHour != null //
                && thisHour.getDateTime().isAfter(hour.minusHours(1)) //
                && nextHour.getDateTime().isAfter(hour)) {
            String hourVal = String.valueOf(nextHour.doubleVal() - thisHour.doubleVal());
            return new DataPoint(hourlyMetric(metric), hour, hourVal, DataPoint.OK);
        } else {
            return null;
        }
    }
}
