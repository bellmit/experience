package etsdb.meter;

import etsdb.domains.DataPoint;
import etsdb.services.MetricService;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import static etsdb.services.MetricService.*;
import static etsdb.util.DateUtils.isWholeDay;
import static etsdb.util.DateUtils.isWholeHour;
import static java.time.LocalDateTime.of;

public abstract class FlowMeter extends Meter {
    public FlowMeter(String metric, MetricService metricService) {
        super(metric(metric), metricService);
    }

    public String minutelyMetric() {
        return metric + MINUTELY_SUFFIX;
    }

    public String minute5lyMetric() {
        return metric + MINUTE5LY_SUFFIX;
    }

    public String minute10lyMetric() {
        return metric + MINUTE10LY_SUFFIX;
    }

    public String minute15lyMetric() {
        return metric + MINUTE15LY_SUFFIX;
    }

    public String minute20lyMetric() {
        return metric + MINUTE20LY_SUFFIX;
    }

    public String minute30lyMetric() {
        return metric + MINUTE30LY_SUFFIX;
    }

    public String hourlyMetric() {
        return metric + HOURLY_SUFFIX;
    }

    public String dailyMetric() {
        return metric + DAILY_SUFFIX;
    }

    public String monthlyMetric() {
        return metric + MONTHLY_SUFFIX;
    }

    public String yearlyMetric() {
        return metric + YEARLY_SUFFIX;
    }

    public OptionalDouble minus(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            LocalDateTime tmp = start;
            start = end;
            end = tmp;
        }

        Optional<DataPoint> startDp = sample(start);
        Optional<DataPoint> endDp = sample(end);
        if (startDp.isPresent() && endDp.isPresent()) {
            DataPoint s = startDp.get();
            DataPoint e = endDp.get();
            if (s.isDouble() && e.isDouble()) {
                return OptionalDouble.of(e.doubleVal() - s.doubleVal());
            } else {
                return OptionalDouble.empty();
            }
        } else {
            return OptionalDouble.empty();
        }
    }

    public abstract OptionalDouble minute(LocalDateTime minute);

    public abstract OptionalDouble minute5(LocalDateTime minute);

    public abstract OptionalDouble minute10(LocalDateTime minute);

    public abstract OptionalDouble minute15(LocalDateTime minute);

    public abstract OptionalDouble minute20(LocalDateTime minute);

    public abstract OptionalDouble minute30(LocalDateTime minute);

    public OptionalDouble hour(LocalDateTime hour) {
        return metricService.readDouble(hourlyMetric(), hour.withMinute(0).withSecond(0).withNano(0));
    }

    public OptionalDouble hour(LocalDateTime begin, LocalDateTime end) {
        List<DataPoint> dps = metricService.read(hourlyMetric(), begin, end, true);
        if (!dps.isEmpty()) {
            double sum = dps.stream().filter(DataPoint::isDouble).mapToDouble(DataPoint::doubleVal).sum();
            return OptionalDouble.of(sum);
        } else {
            return OptionalDouble.empty();
        }
    }

    public OptionalDouble day(LocalDate day) {
        return metricService.readDouble(dailyMetric(), LocalDateTime.of(day, LocalTime.MIN));
    }

    public OptionalDouble day(LocalDate begin, LocalDate end) {
        List<DataPoint> dps = metricService.read(dailyMetric(), begin.atStartOfDay(), end.atStartOfDay(), true);
        if (!dps.isEmpty()) {
            double sum = dps.stream().filter(DataPoint::isDouble).mapToDouble(DataPoint::doubleVal).sum();
            return OptionalDouble.of(sum);
        } else {
            return OptionalDouble.empty();
        }
    }

    public OptionalDouble month(YearMonth month) {
        LocalDateTime monthMin = of(month.atDay(1), LocalTime.MIN);
        DataPoint monthTotal = metricService.read(monthlyMetric(), monthMin, true);

        if (monthTotal != null) {
            if (monthTotal.isDouble()) {
                return OptionalDouble.of(monthTotal.doubleVal());
            } else {
                return OptionalDouble.empty();
            }
        } else {
            return day(month.atDay(1), month.atEndOfMonth());
        }
    }

    public OptionalDouble month(YearMonth begin, YearMonth end) {
        List<DataPoint> dps = metricService.read(monthlyMetric(), begin.atDay(1).atStartOfDay(), end.atDay(1).atStartOfDay(), true);
        if (!dps.isEmpty()) {
            double sum = dps.stream().filter(DataPoint::isDouble).mapToDouble(DataPoint::doubleVal).sum();
            return OptionalDouble.of(sum);
        } else {
            return day(begin.atDay(1), end.atEndOfMonth());
        }
    }

    public OptionalDouble year(Year year) {
        DataPoint yearTotal = metricService.read(monthlyMetric(), year.atDay(1).atStartOfDay(), true);

        if (yearTotal != null) {
            if (yearTotal.isDouble()) {
                return OptionalDouble.of(yearTotal.doubleVal());
            } else {
                return OptionalDouble.empty();
            }
        } else {
            return day(year.atDay(1), year.atMonth(Month.DECEMBER).atEndOfMonth());
        }
    }

    public OptionalDouble total(LocalDateTime start, LocalDateTime end) {
        if (start.plusHours(2).isAfter(end)) {
            // < 2h
            return minus(start, end);
        } else if (start.plusDays(2).isAfter(end)) {
            // [2h ~ 2d)
            LocalDateTime hourStart = start.plusHours(1).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime hourEnd = end.minusHours(1).withMinute(0).withSecond(0).withNano(0);
            if (hourEnd.isBefore(hourStart)) {
                throw new RuntimeException("minusHourTotal: end - start must >= 2 hours.");
            }

            if (isWholeHour(start)) {
                if (isWholeHour(end)) {
                    return hour(start, hourEnd);
                } else {
                    OptionalDouble hour = hour(start, hourEnd);
                    OptionalDouble total = total(hourEnd.plusHours(1), end);
                    if (hour.isPresent() || total.isPresent()) {
                        return OptionalDouble.of(hour.orElse(0.0) + total.orElse(0.0));
                    } else {
                        return OptionalDouble.empty();
                    }
                }
            } else {
                if (isWholeHour(end)) {
                    OptionalDouble hour = hour(hourStart, hourEnd);
                    OptionalDouble total = total(start, hourStart);
                    if (hour.isPresent() || total.isPresent()) {
                        return OptionalDouble.of(hour.orElse(0.0) + total.orElse(0.0));
                    } else {
                        return OptionalDouble.empty();
                    }
                } else {
                    OptionalDouble hour = hour(hourStart, hourEnd);
                    OptionalDouble total = total(start, hourStart);
                    OptionalDouble total1 = total(hourEnd.plusHours(1), end);
                    if (hour.isPresent() || total.isPresent() || total1.isPresent()) {
                        return OptionalDouble.of(hour.orElse(0.0) + total.orElse(0.0) + total1.orElse(0.0));
                    } else {
                        return OptionalDouble.empty();
                    }
                }
            }
        } else {
            // [2d ~ ...)
            LocalDate dayStart = start.toLocalDate().plusDays(1);
            LocalDate dayEnd = end.toLocalDate().minusDays(1);
            if (dayEnd.isBefore(dayStart)) {
                throw new RuntimeException("minusHourDayTotal: end - start must >= 2 days.");
            }
            if (isWholeDay(start)) {
                if (isWholeDay(end)) {
                    return day(start.toLocalDate(), dayEnd);
                } else {
                    OptionalDouble day = day(start.toLocalDate(), dayEnd);
                    OptionalDouble total = total(end.toLocalDate().atStartOfDay(), end);
                    if (day.isPresent() || total.isPresent()) {
                        return OptionalDouble.of(day.orElse(0.0) + total.orElse(0.0));
                    } else {
                        return OptionalDouble.empty();
                    }
                }
            } else {
                if (isWholeDay(end)) {
                    OptionalDouble day = day(dayStart, dayEnd);
                    OptionalDouble total = total(start, dayStart.atStartOfDay());
                    if (day.isPresent() || total.isPresent()) {
                        return OptionalDouble.of(day.orElse(0.0) + total.orElse(0.0));
                    } else {
                        return OptionalDouble.empty();
                    }
                } else {
                    OptionalDouble day = day(dayStart, dayEnd);
                    OptionalDouble total = total(start, dayStart.atStartOfDay());
                    OptionalDouble total1 = total(end.toLocalDate().atStartOfDay(), end);
                    if (day.isPresent() || total.isPresent() || total1.isPresent()) {
                        return OptionalDouble.of(day.orElse(0.0) + total.orElse(0.0) + total1.orElse(0.0));
                    } else {
                        return OptionalDouble.empty();
                    }
                }
            }
        }
    }
}
