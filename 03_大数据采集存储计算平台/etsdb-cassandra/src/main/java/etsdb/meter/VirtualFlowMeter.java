package etsdb.meter;

import etsdb.services.MetricService;

import java.time.LocalDateTime;
import java.util.OptionalDouble;

import static etsdb.util.DateUtils.minuteBegin;

public class VirtualFlowMeter extends FlowMeter {
    public VirtualFlowMeter(String metric, MetricService metricService) {
        super(metric, metricService);
    }

    @Override
    public OptionalDouble minute(LocalDateTime minute) {
        return metricService.readDouble(minutelyMetric(), minuteBegin(minute, 1));
    }

    @Override
    public OptionalDouble minute5(LocalDateTime minute) {
        return metricService.readDouble(minute5lyMetric(), minuteBegin(minute, 5));
    }

    @Override
    public OptionalDouble minute10(LocalDateTime minute) {
        return metricService.readDouble(minute10lyMetric(), minuteBegin(minute, 10));
    }

    @Override
    public OptionalDouble minute15(LocalDateTime minute) {
        return metricService.readDouble(minute15lyMetric(), minuteBegin(minute, 15));
    }

    @Override
    public OptionalDouble minute20(LocalDateTime minute) {
        return metricService.readDouble(minute20lyMetric(), minuteBegin(minute, 20));
    }

    @Override
    public OptionalDouble minute30(LocalDateTime minute) {
        return metricService.readDouble(minute30lyMetric(), minuteBegin(minute, 30));
    }
}
