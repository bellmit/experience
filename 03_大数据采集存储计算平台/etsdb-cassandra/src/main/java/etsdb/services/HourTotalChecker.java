package etsdb.services;

import etsdb.domains.DataPoint;

import java.time.LocalDateTime;

public class HourTotalChecker {
    private static final String HOUR_TOTAL_CHECKER_METRIC = "r.hour.total.checker.metric";
    private final MetricService metricService;

    public HourTotalChecker(MetricService metricService) {
        this.metricService = metricService;
    }

    public void enable() {
        enable(LocalDateTime.now());
    }

    public void disable() {
        disable(LocalDateTime.now());
    }

    public boolean isEnable() {
        return isEnable(LocalDateTime.now());
    }

    public void enable(LocalDateTime ldt) {
        metricService.write(new DataPoint(HOUR_TOTAL_CHECKER_METRIC, ldt, Boolean.TRUE.toString(), 192));
    }

    public void disable(LocalDateTime ldt) {
        metricService.write(new DataPoint(HOUR_TOTAL_CHECKER_METRIC, ldt, Boolean.FALSE.toString(), 192));
    }

    public boolean isEnable(LocalDateTime ldt) {
        DataPoint sample = metricService.sample(HOUR_TOTAL_CHECKER_METRIC, ldt);
        if (sample == null) {
            return true;
        } else {
            return Boolean.valueOf(sample.getVal());
        }
    }

    public LocalDateTime latestEnableTime(LocalDateTime ldt) {
        DataPoint sample = metricService.sample(HOUR_TOTAL_CHECKER_METRIC, ldt);
        if (sample == null) {
            return LocalDateTime.MIN;
        } else {
            return sample.getDateTime();
        }
    }
}
