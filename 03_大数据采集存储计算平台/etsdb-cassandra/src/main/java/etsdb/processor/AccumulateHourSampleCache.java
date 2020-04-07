package etsdb.processor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import etsdb.domains.DataPoint;
import etsdb.services.MetricService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

class AccumulateHourSampleCache {
    private MetricService metricService;
    /**
     * key: metric&day
     */
    private Cache<String, DataPoint> cache = CacheBuilder.newBuilder().maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.HOURS).build();

    AccumulateHourSampleCache(MetricService metricService) {
        this.metricService = metricService;
    }

    void put(DataPoint dataPoint) {
        String key = key(dataPoint.getMetric(), sampleDateTime(dataPoint.getDateTime()));
        DataPoint dp = cache.getIfPresent(key);
        if (dp == null || dataPoint.getTimestamp() >= dp.getTimestamp()) {
            cache.put(key, dataPoint);
        }
    }

    /**
     * return the sample of hour
     *
     * @param metric metric
     * @param ldt    ldt in (8:00, 9:00]
     * @return latest data point in (8:00, 9:00] or null
     */
    @Nullable
    DataPoint sample(String metric, LocalDateTime ldt) {
        final LocalDateTime sampleLdt = sampleDateTime(ldt);

        if (sampleLdt.minusHours(1).compareTo(LocalDateTime.now()) >= 0) {
            // current time: 8:30, hourLdt: 10:00, (9:00~10:00] has non data point, so return null
            return null;
        }

        DataPoint result = cache.getIfPresent(key(metric, sampleLdt));
        if (result != null) {
            return result;
        }

        DataPoint sample = metricService.sample(metric, sampleLdt);
        if (sample == null) { // this is the first data point of the metric
            return null;
        }

        if (sample.getDateTime().compareTo(sampleLdt.minusHours(1)) > 0) {
            // sample time in (8:00, 9:00]
            cache.put(key(metric, sampleLdt), sample);
            return sample;
        } else { // sample time <= 8:00
            return null;
        }
    }

    /**
     * @param ldt ldt in (8:00:00, 9:00:00]
     * @return 9:00
     */
    @NotNull
    static LocalDateTime sampleDateTime(LocalDateTime ldt) {
        return ldt.minusNanos(1).plusHours(1).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * @param ldt ldt in (8:00:00, 9:00:00]
     * @return 8:00
     */
    @NotNull
    static LocalDateTime hourDateTime(LocalDateTime ldt) {
        return sampleDateTime(ldt).minusHours(1);
    }

    private static String key(String metric, LocalDateTime ldt) {
        return String.format("%s&%s", metric, ldt.format(DateTimeFormatter.ofPattern("yyMMddHH")));
    }
}
