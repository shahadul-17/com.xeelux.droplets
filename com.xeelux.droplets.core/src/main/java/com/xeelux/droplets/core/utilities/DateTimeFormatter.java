package com.xeelux.droplets.core.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTimeFormatter {

    private static final double EPSILON = 0.00001;
    private static final String DEFAULT_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static String formatTime(double time, String unit) {
        time = Math.round((time + EPSILON) * 100) / 100.0;

        return time + " " + unit;
    }

    /**
     * Formats the given time in milliseconds.
     * @param timeInMilliseconds Time (in milliseconds) to format.
     * @return The given time in a human-readable format.
     */
    public static String formatTime(final double timeInMilliseconds) {
        var time = timeInMilliseconds;

        if (time < 1_000) { return formatTime(time, "ms"); }

        time /= 1_000;                               // converts milliseconds to seconds...

        if (time < 60) { return formatTime(time, "seconds"); }

        time /= 60;                                  // converts seconds to minutes...

        if (time < 60) { return formatTime(time, "minutes"); }

        time /= 60;                                  // converts minutes to hours...

        if (time < 24) { return formatTime(time, "hours"); }

        time /= 24;                                  // converts hours to days...

        if (time < 30) { return formatTime(time, "days"); }

        time /= 30;                                  // converts days to months...

        if (time < 12) { return formatTime(time, "months"); }

        time /= 12;                                  // converts months to years...

        return formatTime(time, "years");       // finally returns formatted time as years...
    }

    /**
     * Formats the given time in milliseconds.
     * @param timeInMilliseconds Time (in milliseconds) to format.
     * @return The given time in a human-readable format.
     */
    public static String formatTime(final long timeInMilliseconds) {
        return formatTime(timeInMilliseconds * 1.0);
    }

    public static DateFormat createDateFormat() {
        return createDateFormat(DEFAULT_DATE_TIME_FORMAT_PATTERN);
    }

    public static DateFormat createDateFormat(final String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public static Date parseDate(final String date, final String pattern) {
        final var dateFormat = createDateFormat(pattern);

        try {
            return dateFormat.parse(date);
        } catch (final Exception exception) {
            return null;
        }
    }

    public static Date parseDate(final String date) {
        return parseDate(date, DEFAULT_DATE_TIME_FORMAT_PATTERN);
    }

    public static String formatDate(final Date date, final String pattern) {
        return createDateFormat(pattern).format(date);
    }

    public static String formatDate(final Date date) {
        return formatDate(date, DEFAULT_DATE_TIME_FORMAT_PATTERN);
    }
}
