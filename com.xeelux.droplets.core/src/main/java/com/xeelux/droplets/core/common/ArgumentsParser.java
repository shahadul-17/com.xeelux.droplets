package com.xeelux.droplets.core.common;

import com.xeelux.droplets.core.utilities.NumberUtilities;
import com.xeelux.droplets.core.utilities.StringUtilities;

import java.util.HashMap;
import java.util.Map;

public final class ArgumentsParser {

    private static String profile;          // <-- current profile...
    private static final String ARGUMENT_NAME_PREFIX = "--";
    private static final String DEFAULT_PROFILE = "development";
    private static final Map<String, String> argumentsMap = new HashMap<>();

    /**
     * Populates the arguments.
     * @param arguments Arguments that shall be processed.
     */
    public static void populateArguments(final String[] arguments) {
        // if arguments is null, we shall not proceed any further...
        if (arguments == null) { return; }

        // iterates over all the arguments...
        for (var i = 0; i < arguments.length; ++i) {
            // picking argument of current iteration/index...
            final var argument = arguments[i];

            // if the argument does not start with the prefix,
            // we shall skip this iteration...
            if (!argument.startsWith(ARGUMENT_NAME_PREFIX)) { continue; }

            // incrementing 'i' to point to the next index...
            ++i;

            // after increment, if 'i' is greater than or
            // equal to the length of the arguments array,
            // we shall skip this iteration...
            if (i >= arguments.length) { continue; }

            // otherwise, we shall take the argument name by removing prefix from the argument...
            final var argumentName = argument.substring(ARGUMENT_NAME_PREFIX.length());
            // and take the argument value from the index next to the argument name...
            final var argumentValue = arguments[i];        // next argument is the value...

            // we shall place the argument name-value pair to the arguments map...
            argumentsMap.put(argumentName, argumentValue);
        }
    }

    public static String getProfile() {
        // if profile is null...
        if (StringUtilities.isNull(profile)) {
            // we shall retrieve profile argument...
            profile = ArgumentsParser.getArgument("profile", DEFAULT_PROFILE)
                    .toLowerCase();
        }

        // otherwise, we shall return the preloaded profile...
        return profile;
    }

    /**
     * Retrieves command-line argument value by name.
     * @param argumentName Command-line argument name.
     * @return Command-line argument value.
     */
    public static String getArgument(final String argumentName) {
        return getArgument(argumentName, StringUtilities.getEmptyString());
    }

    /**
     * Retrieves command-line argument value by name.
     * @param argumentName Command-line argument name.
     * @param defaultValue Default value is returned if
     *                     the specified argument is not available.
     * @return Command-line argument value.
     */
    public static String getArgument(final String argumentName, final String defaultValue) {
        final var argumentValue = argumentsMap.get(argumentName);

        return StringUtilities.getDefaultIfNullOrWhiteSpace(argumentValue, defaultValue, true);
    }

    public static char getArgumentAsCharacter(final String argumentName) {
        final var argument = getArgument(argumentName);

        if (StringUtilities.isEmpty(argument)) { return '\0'; }

        return argument.charAt(0);
    }

    public static boolean getArgumentAsBoolean(final String argumentName) {
        return "true".equals(getArgument(argumentName));
    }

    public static boolean getArgumentAsBoolean(final String argumentName, final boolean defaultValue) {
        final var argument = getArgument(argumentName, "" + defaultValue);

        return "true".equals(argument);
    }

    public static byte getArgumentAsByte(final String argumentName) {
        return Byte.parseByte(getArgument(argumentName));
    }

    public static short getArgumentAsShort(final String argumentName) {
        return Short.parseShort(getArgument(argumentName));
    }

    public static int getArgumentAsInteger(final String argumentName, final int defaultValue) {
        final var argumentValue = getArgument(argumentName);

        // if the argument value is not found, we shall return the default value...
        if (StringUtilities.isEmpty(argumentValue)) { return defaultValue; }

        // otherwise, tries to parse value as integer. If fails, returns
        // the default value...
        return NumberUtilities.tryParseInteger(argumentValue, defaultValue);
    }

    public static long getArgumentAsLong(final String argumentName, final long defaultValue) {
        final var argumentValue = getArgument(argumentName);

        // if the argument value is not found, we shall return the default value...
        if (StringUtilities.isEmpty(argumentValue)) { return defaultValue; }

        // otherwise, tries to parse value as long. If fails, returns
        // the default value...
        return NumberUtilities.tryParseLong(argumentValue, defaultValue);
    }

    public static float getArgumentAsFloat(final String argumentName) {
        return Float.parseFloat(getArgument(argumentName));
    }

    public static double getArgumentAsDouble(final String argumentName, final double defaultValue) {
        final var argumentValue = getArgument(argumentName);

        // if the argument value is not found, we shall return the default value...
        if (StringUtilities.isEmpty(argumentValue)) { return defaultValue; }

        // otherwise, tries to parse value as double. If fails, returns
        // the default value...
        return NumberUtilities.tryParseDouble(argumentValue, defaultValue);
    }
}
