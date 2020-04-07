package etsdb.meter;

import etsdb.domains.DataPoint;
import etsdb.services.MetricService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import static etsdb.util.DateUtils.minuteBegin;

public class Meter {
    public final String metric;
    protected final MetricService metricService;

    public Meter(String metric, MetricService metricService) {
        this.metric = metric;
        this.metricService = metricService;
    }

    public void write(DataPoint dp) {
        metricService.write(dp);
    }

    public CompletableFuture<Void> writeAsync(DataPoint dp) {
        return CompletableFuture.runAsync(() -> write(dp));
    }

    public void write(List<DataPoint> dps) {
        metricService.write(dps);
    }

    public CompletableFuture<Void> writeAsync(List<DataPoint> dps) {
        return CompletableFuture.runAsync(() -> write(dps));
    }

    public void delete(LocalDateTime start, LocalDateTime end) {
        metricService.delete(metric, start, end);
    }

    public CompletableFuture<Void> deleteAsync(LocalDateTime start, LocalDateTime end) {
        return CompletableFuture.runAsync(() -> delete(start, end));
    }

    public Optional<DataPoint> read(LocalDateTime ldt) {
        return Optional.ofNullable(metricService.read(metric, ldt));
    }

    public Optional<DataPoint> read(LocalDateTime ldt, boolean onlyValid) {
        return Optional.ofNullable(metricService.read(metric, ldt, onlyValid));
    }

    public List<DataPoint> read(LocalDateTime start, LocalDateTime end) {
        return metricService.read(metric, start, end, true);
    }

    public CompletableFuture<List<DataPoint>> readAsync(LocalDateTime start, LocalDateTime end) {
        return CompletableFuture.supplyAsync(() -> read(start, end));
    }

    public List<DataPoint> read(LocalDateTime start, LocalDateTime end, boolean onlyValid) {
        return metricService.read(metric, start, end, onlyValid);
    }

    public CompletableFuture<List<DataPoint>> readAsync(LocalDateTime start, LocalDateTime end, boolean onlyValid) {
        return CompletableFuture.supplyAsync(() -> read(start, end, onlyValid));
    }

    public Optional<DataPoint> sample() {
        return Optional.ofNullable(metricService.sample(metric));
    }

    public Optional<DataPoint> sample(LocalDateTime ldt) {
        return Optional.ofNullable(metricService.sample(metric, ldt));
    }

    public Optional<DataPoint> sample(LocalDateTime ldt, boolean valid) {
        return Optional.ofNullable(metricService.sample(metric, ldt, valid));
    }

    public Optional<DataPoint> sample(LocalDateTime ldt, boolean valid, boolean forward) {
        return Optional.ofNullable(metricService.sample(metric, ldt, valid, forward));
    }

    public List<DataPoint> sample(LocalDateTime start, LocalDateTime end, int interval) {
        return metricService.sample(metric, start, end, interval);
    }

    public OptionalDouble minuteAround(BiFunction<LocalDateTime, LocalDateTime, OptionalDouble> fun, LocalDateTime ldt, int minutes) {
        LocalDateTime begin = minuteBegin(ldt, minutes);
        LocalDateTime end = begin.plusMinutes(minutes);
        return fun.apply(begin, end);
    }
}
