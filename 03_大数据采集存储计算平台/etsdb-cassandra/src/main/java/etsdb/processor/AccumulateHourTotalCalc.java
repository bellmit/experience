package etsdb.processor;

import etsdb.domains.DataPoint;
import etsdb.services.MetricService;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

import static etsdb.processor.AccumulateHourSampleCache.hourDateTime;
import static etsdb.processor.AccumulateHourSampleCache.sampleDateTime;
import static etsdb.services.AccumulatedMetricService.HOURLY_SUFFIX;

class AccumulateHourTotalCalc {
    private final MetricService metricService;
    private final AccumulateHourSampleCache hourSampleCache;
    private final DataPoint current;
    private final String metric;
    private final String hourMetric;
    private final LocalDateTime thisHourTotalTime; // 8:00
    private final LocalDateTime nextHourTotalTime; // 9:00
    private final DataPoint preSample; // 08:00
    private final DataPoint thisSample; // 09:00
    private final DataPoint nextSample; // 10:00

    AccumulateHourTotalCalc(MetricService metricService, AccumulateHourSampleCache hourSampleCache,
                            DataPoint current) {
        this.metricService = metricService;
        this.hourSampleCache = hourSampleCache;
        this.current = current;
        metric = current.getMetric();
        hourMetric = metric + HOURLY_SUFFIX;

        LocalDateTime dateTime = current.getDateTime(); // (8:00 ~ 9:00]
        thisHourTotalTime = hourDateTime(dateTime);
        nextHourTotalTime = sampleDateTime(dateTime);

        preSample = hourSampleCache.sample(metric, thisHourTotalTime);
        thisSample = hourSampleCache.sample(metric, nextHourTotalTime);
        nextSample = hourSampleCache.sample(metric, nextHourTotalTime.plusHours(1));
    }

    @Nullable
    DataPoint thisHourTotal() {
        if (thisSample == null || current.getTimestamp() >= thisSample.getTimestamp()) {
            hourSampleCache.put(current);
        }

        if (thisSample == null) { // 8:00~9:00 non data point
            if (preSample != null) { // 8:04 - 7:59
                double d = current.doubleVal() - preSample.doubleVal();
                return new DataPoint(hourMetric, thisHourTotalTime, String.valueOf(d), DataPoint.OK);
            } else { // (7:00~8:00] non data point
                return null;
            }
        } else {
            if (preSample != null) { // 8:59 - 7:59
                if (current.getTimestamp() >= thisSample.getTimestamp()) { // new hour sample // 8:59 - 7:59
                    double d = current.doubleVal() - preSample.doubleVal();
                    return new DataPoint(hourMetric, thisHourTotalTime, String.valueOf(d), DataPoint.OK);
                } else {
                    return null;
                }
            } else { // (7:00~8:00] non data point, use first data point after 8:00, so hourTotal = 8:59
                // - 8:01

                List<DataPoint> dataPoints =
                        metricService.read(metric, thisHourTotalTime.plusNanos(1), nextHourTotalTime, true);

                if (current.getTimestamp() >= thisSample.getTimestamp()) { // new hour sample // 8:59 - 7:59
                    double d = current.doubleVal() - dataPoints.get(0).doubleVal();
                    return new DataPoint(hourMetric, thisHourTotalTime, String.valueOf(d),
                            DataPoint.Quality.GOOD.getVal());
                } else {
                    if (current.getTimestamp() <= dataPoints.get(0).getTimestamp()) {
                        return new DataPoint(hourMetric, thisHourTotalTime, String.valueOf(thisSample
                                .doubleVal() - current.doubleVal()), DataPoint.Quality.GOOD.getVal());
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    @Nullable
    DataPoint nextHourTotal() {
        if (thisSample == null) { // 8:00~9:00 non data point
            if (nextSample != null) { // 9:59 - 8:01 instead of 9:59 - 9:01
                double d = nextSample.doubleVal() - current.doubleVal();
                return new DataPoint(hourMetric, nextHourTotalTime, String.valueOf(d), DataPoint.OK);
            } else { // (9:00~10:00] non data point
                return null;
            }
        } else {
            if (current.getTimestamp() >= thisSample.getTimestamp()) { // new hour sample
                if (nextSample != null) { // 9:59 - 8:59 instead of 9:59 - 9:01 or 9:59 - 8:01
                    double d = nextSample.doubleVal() - current.doubleVal();
                    return new DataPoint(hourMetric, nextHourTotalTime, String.valueOf(d), DataPoint.OK);
                } else { // (9:00~10:00] non data point
                    return null;
                }
            } else { // 8:59 and 8:01 has in db, current value: 8:30, hourTotal not change
                return null;
            }
        }
    }
}
