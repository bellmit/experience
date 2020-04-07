package etsdb.processor;

import etsdb.domains.DataPoint;
import etsdb.services.MetricService;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static etsdb.processor.AccumulateHourSampleCache.hourDateTime;
import static etsdb.services.AccumulatedMetricService.HOURLY_SUFFIX;

class AccumulateAutoRepairHisHourly {
    private MetricService metricService;
    private HourlyMetricWriter hourlyMetricWriter;

    AccumulateAutoRepairHisHourly(MetricService metricService, HourlyMetricWriter hourlyMetricWriter) {
        this.metricService = metricService;
        this.hourlyMetricWriter = hourlyMetricWriter;
    }

    void checkAndRepair(@NotNull DataPoint dataPoint) {
        String metric = dataPoint.getMetric();

        DataPoint sample = metricService.sample(metric, dataPoint.getDateTime());
        if (sample != null && !dataPoint.equals(sample)) {
            LocalDateTime thisHour = hourDateTime(dataPoint.getDateTime());
            LocalDateTime sampleHour = hourDateTime(sample.getDateTime());
            if (thisHour.minusHours(1).isAfter(sampleHour)) {
                long hours =
                        Duration.between(sampleHour, thisHour).get(ChronoUnit.SECONDS)
                                / Duration.ofHours(1).getSeconds();
                double hourTotal = (dataPoint.doubleVal() - sample.doubleVal()) / (hours - 1);
                for (LocalDateTime ldt = sampleHour.plusHours(1); ldt.isBefore(thisHour); ldt =
                        ldt.plusHours(1)) {
                    DataPoint dp =
                            new DataPoint(metric + HOURLY_SUFFIX, ldt, String.valueOf(hourTotal),
                                    DataPoint.Quality.GOOD.getVal());
                    hourlyMetricWriter.write(dp);
                }
            }
        }
    }
}
