package etsdb.meter;

import etsdb.services.MetricService;

import java.time.LocalDateTime;
import java.util.OptionalDouble;

import static etsdb.util.DateUtils.*;

public class ScalarMeter extends Meter {
    public ScalarMeter(String metric, MetricService metricService) {
        super(metric, metricService);
    }

    public OptionalDouble average(LocalDateTime begin, LocalDateTime end) {
        return metricService.average(metric, begin, end);
    }

    public OptionalDouble min(LocalDateTime begin, LocalDateTime end) {
        return metricService.min(metric, begin, end);
    }

    public OptionalDouble max(LocalDateTime begin, LocalDateTime end) {
        return metricService.max(metric, begin, end);
    }

    private OptionalDouble minuteAverage(LocalDateTime ldt, int minutes) {
        return minuteAround(this::average, ldt, minutes);
    }

    private OptionalDouble minuteMin(LocalDateTime ldt, int minutes) {
        return minuteAround(this::min, ldt, minutes);
    }

    private OptionalDouble minuteMax(LocalDateTime ldt, int minutes) {
        return minuteAround(this::max, ldt, minutes);
    }

    public OptionalDouble minuteAverage(LocalDateTime ldt) {
        return minuteAverage(ldt, 1);
    }

    public OptionalDouble minute5Average(LocalDateTime ldt) {
        return minuteAverage(ldt, 5);
    }

    public OptionalDouble minute10Average(LocalDateTime ldt) {
        return minuteAverage(ldt, 10);
    }

    public OptionalDouble minute15Average(LocalDateTime ldt) {
        return minuteAverage(ldt, 15);
    }

    public OptionalDouble minute20Average(LocalDateTime ldt) {
        return minuteAverage(ldt, 20);
    }

    public OptionalDouble minute30Average(LocalDateTime ldt) {
        return minuteAverage(ldt, 30);
    }

    public OptionalDouble hourAverage(LocalDateTime ldt) {
        return average(startOfHour(ldt), endOfHour(ldt));
    }

    public OptionalDouble dayAverage(LocalDateTime ldt) {
        return average(startOfDay(ldt), endOfDay(ldt));
    }

    public OptionalDouble monthAverage(LocalDateTime ldt) {
        return average(startOfMonth(ldt), endOfMonth(ldt));
    }

    public OptionalDouble yearAverage(LocalDateTime ldt) {
        return average(startOfYear(ldt), endOfYear(ldt));
    }

    public OptionalDouble minuteMin(LocalDateTime ldt) {
        return minuteMin(ldt, 1);
    }

    public OptionalDouble minute5Min(LocalDateTime ldt) {
        return minuteMin(ldt, 5);
    }

    public OptionalDouble minute10Min(LocalDateTime ldt) {
        return minuteMin(ldt, 10);
    }

    public OptionalDouble minute15Min(LocalDateTime ldt) {
        return minuteMin(ldt, 15);
    }

    public OptionalDouble minute20Min(LocalDateTime ldt) {
        return minuteMin(ldt, 20);
    }

    public OptionalDouble minute30Min(LocalDateTime ldt) {
        return minuteMin(ldt, 30);
    }

    public OptionalDouble hourMin(LocalDateTime ldt) {
        return min(startOfHour(ldt), endOfHour(ldt));
    }

    public OptionalDouble dayMin(LocalDateTime ldt) {
        return min(startOfDay(ldt), endOfDay(ldt));
    }

    public OptionalDouble monthMin(LocalDateTime ldt) {
        return min(startOfMonth(ldt), endOfMonth(ldt));
    }

    public OptionalDouble yearMin(LocalDateTime ldt) {
        return min(startOfYear(ldt), endOfYear(ldt));
    }

    public OptionalDouble minuteMax(LocalDateTime ldt) {
        return minuteMax(ldt, 1);
    }

    public OptionalDouble minute5Max(LocalDateTime ldt) {
        return minuteMax(ldt, 5);
    }

    public OptionalDouble minute10Max(LocalDateTime ldt) {
        return minuteMax(ldt, 10);
    }

    public OptionalDouble minute15Max(LocalDateTime ldt) {
        return minuteMax(ldt, 15);
    }

    public OptionalDouble minute20Max(LocalDateTime ldt) {
        return minuteMax(ldt, 20);
    }

    public OptionalDouble minute30Max(LocalDateTime ldt) {
        return minuteMax(ldt, 30);
    }

    public OptionalDouble hourMax(LocalDateTime ldt) {
        return max(startOfHour(ldt), endOfHour(ldt));
    }

    public OptionalDouble dayMax(LocalDateTime ldt) {
        return max(startOfDay(ldt), endOfDay(ldt));
    }

    public OptionalDouble monthMax(LocalDateTime ldt) {
        return max(startOfMonth(ldt), endOfMonth(ldt));
    }

    public OptionalDouble yearMax(LocalDateTime ldt) {
        return max(startOfYear(ldt), endOfYear(ldt));
    }
}
