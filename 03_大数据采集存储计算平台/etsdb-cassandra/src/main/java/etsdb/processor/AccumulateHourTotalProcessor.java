package etsdb.processor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import etsdb.domains.DataPoint;
import etsdb.services.HourTotalChecker;
import etsdb.services.MetricService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class AccumulateHourTotalProcessor {
    private final HourTotalChecker hourTotalChecker;
    private MetricService metricService;
    private HourlyMetricWriter hourlyMetricWriter;

    /**
     * key: hourMetric
     */
    private Cache<String, Double> cache = CacheBuilder.newBuilder().maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.DAYS).build();

    AccumulateHourTotalProcessor(MetricService metricService, HourlyMetricWriter hourlyMetricWriter) {
        this.metricService = metricService;
        this.hourlyMetricWriter = hourlyMetricWriter;
        hourTotalChecker = new HourTotalChecker(metricService);
    }

    void process(DataPoint hourTotal) {
        if (hourTotal != null) {
            if (isLeap(hourTotal)) {
                Double avg = getAvg(hourTotal);
                if (avg != null) {
                    hourTotal.setVal(avg.toString());
                    hourTotal.setQuality(DataPoint.Quality.GOOD);
                    hourlyMetricWriter.write(hourTotal);
                } else {
                    hourlyMetricWriter.write(hourTotal);
                }
            } else {
                hourlyMetricWriter.write(hourTotal);
            }
        }
    }

    private boolean isLeap(@NotNull DataPoint hourTotal) {
        if (hourTotal.doubleVal() == 0) {
            return false;
        } else {
            Double avg = getAvg(hourTotal);
            if (avg != null) {
                return hourTotal.doubleVal() > avg * 100;
            } else {
                return false;
            }
        }
    }

    private Double getAvg(DataPoint hourTotal) {
        String metric = hourTotal.getMetric();
        Double avg = cache.getIfPresent(metric);
        if (avg != null) {
            return avg;
        } else {
            avg = avg(hourTotal);
            if (avg != null) {
                cache.put(metric, avg);
                return avg;
            } else {
                return null;
            }
        }
    }

    private Double avg(DataPoint hourTotal) {
        LocalDateTime dateTime = hourTotal.getDateTime();
        if (hourTotalChecker.isEnable(dateTime)) {
            LocalDateTime latestEnableTime = hourTotalChecker.latestEnableTime(dateTime);
            LocalDateTime oneMonthsAgo = dateTime.minusMonths(1);
            LocalDateTime beginTime = oneMonthsAgo.isAfter(latestEnableTime) ? oneMonthsAgo : latestEnableTime;
            List<DataPoint> points =
                    metricService.read(hourTotal.getMetric(), beginTime, dateTime, true);

            List<DataPoint> list =
                    points.stream().filter(dp -> dp.doubleVal() != 0).collect(Collectors.toList());

            if (list.size() < 24 * 15) { // if the hour data point less than one week, ignore
                return null;
            } else {
                return calcValidAvg(list);
            }
        } else {
            return null;
        }
    }

    @Nullable
    private Double calcValidAvg(List<DataPoint> list) {
        list.sort(Comparator.comparingDouble(dp -> Math.abs(dp.doubleVal())));

        double sum = 0;
        int count = 0;
        Double validAvg = null;
        for (int i = 0; i < list.size() - 1; i++) {
            DataPoint thisDp = list.get(i);
            DataPoint nextDp = list.get(i + 1);

            sum = sum + Math.abs(thisDp.doubleVal());
            count = count + 1;
            validAvg = sum / count;

            if (i > list.size() * 0.5 && Math.abs(nextDp.doubleVal()) / validAvg > 300) {
                for (int j = i + 1; j < list.size(); j++) {
                    DataPoint skipHourDp = list.get(j);
                    skipHourDp.setVal(validAvg.toString());
                    hourlyMetricWriter.write(skipHourDp);
                }
                break;
            }
        }

        return validAvg;
    }
}
