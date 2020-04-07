package etsdb.processor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import etsdb.domains.DataPoint;
import etsdb.services.MetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static etsdb.services.MetricService.dailyMetric;

class HourlyMetricWriter {
    private static final Logger logger = LoggerFactory.getLogger(HourlyMetricWriter.class);
    private RepairRecorder repairRecorder;
    /**
     * key: hourMetric&day
     */
    private Cache<String, Map<LocalDateTime, DataPoint>> dailyHourCache = CacheBuilder.newBuilder()
            .maximumSize(10000).expireAfterWrite(1, TimeUnit.DAYS).build();

    private MetricService metricService;

    HourlyMetricWriter(MetricService metricService, RepairRecorder repairRecorder) {
        this.metricService = metricService;
        this.repairRecorder = repairRecorder;
    }

    void write(DataPoint hourTotal) {
        writeDayTotal(hourTotal);
        metricService.write(hourTotal);
        repairRecorder.add(hourTotal.getMetric(), hourTotal.getDateTime());
    }

    private void writeDayTotal(DataPoint hourTotal) {
        try {
            String hourMetric = hourTotal.getMetric();
            LocalDateTime hourDateTime = hourTotal.getDateTime();
            Map<LocalDateTime, DataPoint> hourlyMap =
                    dailyHourCache.get(
                            key(hourMetric, hourDateTime),
                            () -> {
                                List<DataPoint> points =
                                        metricService.read(hourMetric, hourDateTime.withHour(0),
                                                hourDateTime.withHour(23), true);
                                return points.stream().collect(
                                        Collectors.toMap(DataPoint::getDateTime, Function.identity()));
                            });
            hourlyMap.put(hourDateTime, hourTotal);
            List<DataPoint> dps =
                    hourlyMap.values().stream().filter(Objects::nonNull).collect(Collectors.toList());

            double dayTotal = dps.stream().mapToDouble(DataPoint::doubleVal).sum();

            DataPoint day =
                    new DataPoint(dailyMetric(hourMetric), hourDateTime.withHour(0),
                            String.valueOf(dayTotal), DataPoint.OK);
            metricService.write(day);
            repairRecorder.add(day.getMetric(), day.getDateTime());
        } catch (ExecutionException e) {
            logger.error("error when get metric daily hour cache: " + hourTotal.getMetric(), e);
        }
    }

    private static String key(String metric, LocalDateTime ldt) {
        return String.format("%s&%s", metric, ldt.format(DateTimeFormatter.ofPattern("yyMMddHH")));
    }
}
