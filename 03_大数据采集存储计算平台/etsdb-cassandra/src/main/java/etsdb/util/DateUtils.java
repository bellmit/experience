package etsdb.util;


import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    private DateUtils() {
    }

    @NotNull
    public static LocalDateTime toLdt(@NotNull Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    @NotNull
    public static Date toDate(@NotNull LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static boolean isWholeHour(LocalDateTime ldt) {
        return ldt.getMinute() == 0 && ldt.getSecond() == 0 && ldt.getNano() == 0;
    }

    public static boolean isWholeDay(LocalDateTime ldt) {
        return ldt.getHour() == 0 && isWholeHour(ldt);
    }

    public static LocalDateTime minuteBegin(LocalDateTime minute, int minutes) {
        Preconditions.checkArgument(minutes > 0, "minutes must > 0");
        return minute.withMinute(minute.getMinute() - minute.getMinute() % minutes).withSecond(0).withNano(0);
    }

    public static LocalDateTime startOfHour(LocalDateTime ldt) {
        return ldt.withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDateTime startOfNextHour(LocalDateTime ldt) {
        return startOfHour(ldt).plusHours(1);
    }

    public static LocalDateTime endOfHour(LocalDateTime ldt) {
        return startOfNextHour(ldt).minusSeconds(1);
    }

    public static LocalDateTime startOfDay(LocalDateTime ldt) {
        return ldt.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime startOfNextDay(LocalDateTime ldt) {
        return startOfDay(ldt).plusDays(1);
    }

    public static LocalDateTime endOfDay(LocalDateTime ldt) {
        return startOfNextDay(ldt).minusSeconds(1);
    }

    public static LocalDateTime startOfMonth(LocalDateTime ldt) {
        return YearMonth.from(ldt).atDay(1).atStartOfDay();
    }

    public static LocalDateTime startOfNextMonth(LocalDateTime ldt) {
        return YearMonth.from(ldt).plusMonths(1).atDay(1).atStartOfDay();
    }

    public static LocalDateTime endOfMonth(LocalDateTime ldt) {
        return startOfNextMonth(ldt).minusSeconds(1);
    }

    public static LocalDateTime startOfYear(LocalDateTime ldt) {
        return Year.of(ldt.getYear()).atDay(1).atStartOfDay();
    }

    public static LocalDateTime startOfNextYear(LocalDateTime ldt) {
        return Year.of(ldt.getYear()).plusYears(1).atDay(1).atStartOfDay();
    }

    public static LocalDateTime endOfYear(LocalDateTime ldt) {
        return startOfNextYear(ldt).minusSeconds(1);
    }
}
