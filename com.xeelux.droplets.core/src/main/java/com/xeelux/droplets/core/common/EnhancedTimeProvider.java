package com.xeelux.droplets.core.common;

public final class EnhancedTimeProvider {

    private static final int UNIX_EPOCH_YEAR = 1970;
    private static final int ENHANCED_EPOCH_YEAR = 2024;
    private static final int EPOCH_DIFFERENCE_IN_YEARS
            = ENHANCED_EPOCH_YEAR - UNIX_EPOCH_YEAR;
    private static final long EPOCH_DIFFERENCE_IN_MILLISECONDS
            = EPOCH_DIFFERENCE_IN_YEARS * 365 * 24 * 60 * 60 * 1000L;

    /**
     * Returns the epoch difference (in milliseconds)
     * between the unix epoch year and the enhanced epoch year.
     * @return The epoch difference in milliseconds.
     */
    public static long getEpochDifferenceInMilliseconds() {
        return EPOCH_DIFFERENCE_IN_MILLISECONDS;
    }

    /**
     * Returns the current time in milliseconds (enhanced).
     * @implNote This is an enhanced version of the
     * System.currentTimeMillis() method which measures
     * the time difference between the current time and
     * midnight, January 1, 2024 UTC.
     * @return The difference, measured in milliseconds,
     * between the current time and midnight, January 1, 2024 UTC.
     */
    public static long getCurrentTimeInMilliseconds() {
        // getting the current time in milliseconds...
        final var currentTimeInMilliseconds = System.currentTimeMillis();
        // converts the current unix time in milliseconds into enhanced time...
        final var enhancedCurrentTimeInMilliseconds
                = fromUnixTimeInMilliseconds(currentTimeInMilliseconds);

        // finally we shall return the enhanced current time in milliseconds...
        return enhancedCurrentTimeInMilliseconds;
    }

    /**
     * Converts the given enhanced time in milliseconds
     * into the unix time in milliseconds.
     * @param enhancedTimeInMilliseconds Enhanced time in milliseconds to convert.
     * @return Unix time in milliseconds.
     */
    public static long toUnixTimeInMilliseconds(long enhancedTimeInMilliseconds) {
        return enhancedTimeInMilliseconds + EPOCH_DIFFERENCE_IN_MILLISECONDS;
    }

    /**
     * Converts the given unix time in milliseconds
     * into enhanced time in milliseconds.
     * @param unixTimeInMilliseconds Unix time in milliseconds to convert.
     * @return Enhanced time in milliseconds.
     */
    public static long fromUnixTimeInMilliseconds(long unixTimeInMilliseconds) {
        return unixTimeInMilliseconds - EPOCH_DIFFERENCE_IN_MILLISECONDS;
    }
}
