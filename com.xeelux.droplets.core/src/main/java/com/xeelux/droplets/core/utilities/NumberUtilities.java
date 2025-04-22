package com.xeelux.droplets.core.utilities;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NumberUtilities {

    private static final Logger logger = LogManager.getLogger(NumberUtilities.class);

    /**
     * Tries to parse a string value as integer.
     * @param value String value to be parsed.
     * @return The integer value if succeeds. Otherwise,
     * returns Integer.MIN_VALUE.
     */
    public static int tryParseInteger(final String value) {
        return tryParseInteger(value, Integer.MIN_VALUE);
    }

    /**
     * Tries to parse a string value as integer.
     * @param value String value to be parsed.
     * @param defaultValue Default value to be returned if exception occurs.
     * @return The integer value if succeeds. Otherwise,
     * returns the default value.
     */
    public static int tryParseInteger(String value, final int defaultValue) {
        // sanitizing the value before parsing...
        value = StringUtilities.getDefaultIfNullOrWhiteSpace(
                value, StringUtilities.getEmptyString(), true);

        // if no value is found, we shall return the default value...
        if (StringUtilities.isEmpty(value)) { return defaultValue; }

        var numericValue = defaultValue;

        try {
            // tries to parse the value as integer...
            numericValue = Integer.parseInt(value);
        } catch (final Exception exception) {
            logger.log(Level.WARN, "An exception occurred while parsing '{}' as integer.", value, exception);
        }

        // returns the numeric value...
        return numericValue;
    }

    /**
     * Tries to parse a string value as long.
     * @param value String value to be parsed.
     * @return The long value if succeeds. Otherwise,
     * returns Long.MIN_VALUE.
     */
    public static long tryParseLong(final String value) {
        return tryParseLong(value, Long.MIN_VALUE);
    }

    /**
     * Tries to parse a string value as long.
     * @param value String value to be parsed.
     * @param defaultValue Default value to be returned if exception occurs.
     * @return The long value if succeeds. Otherwise,
     * returns the default value.
     */
    public static long tryParseLong(final String value, final long defaultValue) {
        var numericValue = defaultValue;

        try {
            // tries to parse the value as long...
            numericValue = Long.parseLong(value);
        } catch (final Exception exception) {
            logger.log(Level.WARN, "An exception occurred while parsing '{}' as long.", value, exception);
        }

        // returns the numeric value...
        return numericValue;
    }

    /**
     * Tries to parse a string value as double.
     * @param value String value to be parsed.
     * @return The double value if succeeds. Otherwise,
     * returns Double.MIN_VALUE.
     */
    public static double tryParseDouble(final String value) {
        return tryParseDouble(value, Double.MIN_VALUE);
    }

    /**
     * Tries to parse a string value as double.
     * @param value String value to be parsed.
     * @param defaultValue Default value to be returned if an exception occurs.
     * @return The double value if succeeds. Otherwise,
     * returns the default value.
     */
    public static double tryParseDouble(final String value, final double defaultValue) {
        var numericValue = defaultValue;

        try {
            // tries to parse the value as double...
            numericValue = Double.parseDouble(value);
        } catch (final Exception exception) {
            logger.log(Level.WARN, "An exception occurred while parsing '{}' as double.", value, exception);
        }

        // returns the numeric value...
        return numericValue;
    }
}
