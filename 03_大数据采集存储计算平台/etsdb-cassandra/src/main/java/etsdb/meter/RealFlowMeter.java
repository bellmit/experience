package etsdb.meter;

import etsdb.services.MetricService;

import java.time.LocalDateTime;
import java.util.OptionalDouble;

public class RealFlowMeter extends FlowMeter {
    public RealFlowMeter(String metric, MetricService metricService) {
        super(metric, metricService);
    }

    @Override
    public OptionalDouble minute(LocalDateTime minute) {
        return minute(minute, 1);
    }

    @Override
    public OptionalDouble minute5(LocalDateTime minute) {
        return minute(minute, 5);
    }

    @Override
    public OptionalDouble minute10(LocalDateTime minute) {
        return minute(minute, 10);
    }

    @Override
    public OptionalDouble minute15(LocalDateTime minute) {
        return minute(minute, 15);
    }

    @Override
    public OptionalDouble minute20(LocalDateTime minute) {
        return minute(minute, 20);
    }

    @Override
    public OptionalDouble minute30(LocalDateTime minute) {
        return minute(minute, 30);
    }

    private OptionalDouble minute(LocalDateTime minute, int minutes) {
        return minuteAround(this::minus, minute, minutes);
    }
}
