package etsdb.util;

import java.time.format.DateTimeFormatter;

import static java.lang.String.format;

public class DateTimePattern {
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String HOUR_FORMAT = "yyyy-MM-dd HH:00:00";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    public static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern(HOUR_FORMAT);

    public static final String YEAR_PATTERN = "^[12][0-9]{3}$"; // yyyy, 1000~2999
    public static final String MONTH_PATTERN = "^[12][0-9]{3}-(0?[1-9]|1[0-2])$";
    private static final String JAN_PATTERN = "(0?[13578]|1[02])-(0?[1-9]|[12][0-9]|3[01])";
    private static final String FEB_PATTERN = "0?2-(0?[1-9]|[12][0-9])";
    private static final String APR_PATTERN = "(0?[469]|11)-(0?[1-9]|[12][0-9]|30)";
    public static final String DAY_PATTERN = format("^[12][0-9]{3}-(%s|%s|%s)$", JAN_PATTERN,
            FEB_PATTERN, APR_PATTERN);
    public static final String HOUR_PATTERN = format(
            "^[12][0-9]{3}-(%s|%s|%s) ([01][0-9]|2[0-3]):00:00$", JAN_PATTERN, FEB_PATTERN, APR_PATTERN);
    public static final String TIME_PATTERN = format(
            "^[12][0-9]{3}-(%s|%s|%s) ([01][0-9]|2[0-3])(:[0-5][0-9]){2}$", JAN_PATTERN, FEB_PATTERN,
            APR_PATTERN);

    private DateTimePattern() {
    }
}
