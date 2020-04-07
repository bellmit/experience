package etsdb.processor;

import com.google.common.collect.Lists;
import etsdb.domains.DataPoint;
import etsdb.services.MetricService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static etsdb.services.MetricService.dailyMetric;
import static etsdb.services.MetricService.hourlyMetric;

public class BatchProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);
    private final MetricService metricService;
    private final RepairRecorder repairRecorder;
    private final RepairHourlyRecorder repairHourlyRecorder;
    private final SkipRecorder skipRecorder;
    private final boolean enableRepairRecorder;

    public BatchProcessor(MetricService metricService) {
        this(metricService, true);
    }

    public BatchProcessor(MetricService metricService, boolean enableRepairRecorder) {
        this.metricService = metricService;
        this.repairRecorder = RepairRecorder.getInstance(metricService);
        this.repairHourlyRecorder = RepairHourlyRecorder.getInstance(metricService);
        this.skipRecorder = SkipRecorder.getInstance(metricService);
        this.enableRepairRecorder = enableRepairRecorder;
    }

    public void process(String metric, List<DataPoint> list) {
        try {
            if (!list.isEmpty()) {
                List<DataPoint> hourDataList = calcHour(metric, list);
                List<DataPoint> repairHourList = repairHour(metric, list);

                final List<DataPoint> allHourlyDp = new ArrayList<>(hourDataList.size() + repairHourList.size());
                allHourlyDp.addAll(hourDataList);
                allHourlyDp.addAll(repairHourList);
                write(allHourlyDp);

                List<DataPoint> dayDataList = calcDay(metric, allHourlyDp);
                write(dayDataList);
            }
        } catch (Exception e) {
            logger.error(String.format("process metric: %s error", metric), e);
        }
    }

    private List<DataPoint> calcDay(String metric, List<DataPoint> allHourlyDp) {
        List<LocalDate> dayList = findDayList(findHourList(allHourlyDp));

        return dayList.parallelStream() //
                .map(day -> calcRealMetricDay(metric, day)) //
                .collect(Collectors.toList());
    }

    private void write(List<DataPoint> allDp) {
        metricService.write(allDp);
        if (enableRepairRecorder) {
            for (DataPoint dp : allDp) {
                repairRecorder.add(dp.getMetric(), dp.getDateTime());
            }
        }
    }

    @NotNull
    private List<DataPoint> repairHour(String metric, List<DataPoint> list) {
        List<DataPoint> checkPoint = findRepairHourlyCheckPoint(metric, list);

        if (!checkPoint.isEmpty()) {
            logger.info(String.format(
                    "find metric: %s check points: %s",
                    metric,
                    checkPoint.stream().map(DataPoint::getDateTime).map(LocalDateTime::toString)
                            .collect(Collectors.joining(", "))));
        }

        List<DataPoint> repairHourList = checkPoint.parallelStream() //
                .map(this::calcRepairHourly) //
                .flatMap(Collection::stream) //
                .collect(Collectors.toList());

        if (!repairHourList.isEmpty()) {
            logger.info(String.format("find metric: %s repair hour list size: %d", metric,
                    repairHourList.size()));
        }

        for (DataPoint dataPoint : repairHourList) {
            repairHourlyRecorder.add(dataPoint.getMetric(), dataPoint.getDateTime());
        }

        return repairHourList;
    }

    private List<DataPoint> calcHour(String metric, List<DataPoint> list) {
        List<LocalDateTime> hourList = findHourList(list);

        return hourList.parallelStream() //
                .map(hour -> metricService.calcRealMetricHour(metric, hour)) //
                .filter(Objects::nonNull) //
                .collect(Collectors.toList());
    }

    @NotNull
    private List<DataPoint> calcRepairHourly(DataPoint dp) {
        String metric = dp.getMetric();

        DataPoint before = metricService.sample(metric, dp.getTimestamp() - 1000);
        if (before != null) {
            LocalDateTime thisHour = hourDateTime(dp.getDateTime());
            LocalDateTime sampleHour = hourDateTime(before.getDateTime());
            long hours =
                    Duration.between(sampleHour, thisHour).get(ChronoUnit.SECONDS)
                            / Duration.ofHours(1).getSeconds();
            logger.info(String.format("metric: %s, sample: %s, current: %s, hours: %d", metric,
                    sampleHour, thisHour, hours));
            if (hours > 1) {
                DataPoint thisHourLatest = metricService.sample(metric, thisHour.plusHours(1));
                if (thisHourLatest == null) {
                    thisHourLatest = dp;
                }
                double hourTotal = (thisHourLatest.doubleVal() - before.doubleVal()) / hours;
                List<DataPoint> repairHourly = Lists.newArrayListWithCapacity(Math.toIntExact(hours));
                for (LocalDateTime ldt = sampleHour.plusHours(1); ldt.isBefore(thisHour)
                        || ldt.isEqual(thisHour); ldt = ldt.plusHours(1)) {
                    repairHourly.add(new DataPoint(hourlyMetric(metric), ldt, String.valueOf(hourTotal),
                            DataPoint.OK));
                }
                return repairHourly;
            } else {
                return Lists.newArrayList();
            }
        } else {
            return Lists.newArrayList();
        }
    }

    @NotNull
    private List<DataPoint> findRepairHourlyCheckPoint(String metric, List<DataPoint> list) {
        list.sort(Comparator.comparingLong(DataPoint::getTimestamp));
        // check point: check for repair hourly
        List<DataPoint> checkPoint = Lists.newArrayList(list.get(0)); // check first

        // check internal
        for (int i = 1; i < list.size(); i++) {
            DataPoint before = list.get(i - 1);
            DataPoint current = list.get(i);
            if (needRepair(before, current)) {
                checkPoint.add(current);
            }
        }

        // check after last
        DataPoint last = list.get(list.size() - 1);
        DataPoint next = metricService.sample(metric, last.getTimestamp(), true, true);
        if (next != null && needRepair(last, next)) {
            checkPoint.add(next);
        }
        return checkPoint;
    }

    private boolean needRepair(DataPoint before, DataPoint current) {
        LocalDateTime thisHour = hourDateTime(current.getDateTime());
        LocalDateTime sampleHour = hourDateTime(before.getDateTime());
        long hours =
                Duration.between(sampleHour, thisHour).get(ChronoUnit.SECONDS)
                        / Duration.ofHours(1).getSeconds();
        return hours > 1;
    }

    @NotNull
    private static LocalDateTime hourDateTime(LocalDateTime ldt) {
        return ldt.minusNanos(1).withMinute(0).withSecond(0).withNano(0);
    }

    @NotNull
    private List<LocalDateTime> findHourList(List<DataPoint> list) {
        return list.stream() //
                .map(DataPoint::getDateTime) //
                .map(ldt -> ldt.withMinute(0).withSecond(0).withNano(0)) //
                .distinct() //
                .flatMap(ldt -> Stream.of(ldt, ldt.plusHours(1))) //
                .distinct() //
                .collect(Collectors.toList());
    }

    @NotNull
    private List<LocalDate> findDayList(List<LocalDateTime> hourList) {
        return hourList.stream() //
                .map(LocalDateTime::toLocalDate) //
                .distinct() //
                .collect(Collectors.toList());
    }

    @NotNull
    private DataPoint calcRealMetricDay(String metric, LocalDate day) {
        List<DataPoint> dps = metricService.read(hourlyMetric(metric), day.atStartOfDay(), day.atTime(LocalTime.MAX), true);
        if (dps.size() == TimeUnit.DAYS.toHours(1)) {
            List<DataPoint> sortedList = dps.stream() //
                    .filter(dp -> dp.isDouble() && dp.doubleVal() > 0) //
                    .sorted(Comparator.comparingDouble(dp -> Math.abs(dp.doubleVal())))  //
                    .collect(Collectors.toList());
            if (sortedList.size() >= 8) {
                int skipIndex = -1;
                for (int i = sortedList.size() * 3 / 5; i < sortedList.size() - 1; i++) {
                    DataPoint thisDp = sortedList.get(i);
                    DataPoint nextDp = sortedList.get(i + 1);
                    if (Math.abs(nextDp.doubleVal()) / Math.abs(thisDp.doubleVal()) > 100) {
                        skipIndex = i + 1;
                        break;
                    }
                }
                if (skipIndex != -1) {
                    OptionalDouble optionalAverage = sortedList.subList(0, skipIndex).stream().mapToDouble(DataPoint::doubleVal).average();
                    if (optionalAverage.isPresent()) {
                        double avg = optionalAverage.getAsDouble();
                        List<DataPoint> fixedHourly = sortedList.subList(skipIndex, sortedList.size()).stream().peek(dp -> dp.setVal(String.valueOf(avg))).collect(Collectors.toList());
                        fixedHourly.forEach(dp -> skipRecorder.add(dp.getMetric(), dp.getDateTime()));
                        write(fixedHourly);
                        dps = metricService.read(hourlyMetric(metric), day.atStartOfDay(), day.atTime(LocalTime.MAX), true);
                    }
                }
            }
        }
        double dayTotal = dps.stream().mapToDouble(DataPoint::doubleVal).sum();
        return new DataPoint(dailyMetric(metric), day.atStartOfDay(), String.valueOf(dayTotal),
                DataPoint.OK);
    }

}
