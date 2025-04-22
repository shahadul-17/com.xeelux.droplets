package com.xeelux.droplets.core.utilities;

import java.util.Arrays;
import java.util.Date;

public final class StringUtilities {

    private static final int STRING_BUILDER_INITIAL_CAPACITY = 4096;
    private static final String EMPTY_STRING = "";
    private static final String COMMA_SEPARATOR = ",";
    private static final String DEFAULT_OPENING_VALUE_WRAPPER = "\"";
    private static final String DEFAULT_CLOSING_VALUE_WRAPPER = DEFAULT_OPENING_VALUE_WRAPPER;
    private static final String NON_ALPHANUMERIC_CHARACTERS_REGULAR_EXPRESSION = "[^0-9a-zA-Z]";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static String getEmptyString() { return EMPTY_STRING; }

    public static String[] getEmptyStringArray() { return EMPTY_STRING_ARRAY; }

    public static boolean isNull(String text) {
        return text == null;
    }

    public static boolean isEmpty(String text) {
        return getEmptyString().equals(text);
    }

    public static boolean isNullOrEmpty(String text) {
        return isNull(text) || isEmpty(text);
    }

    public static boolean isNullOrWhiteSpace(String text) {
        if (isNull(text)) { return true; }

        return isEmpty(text.trim());
    }

    public static String getDefaultIfNull(String text, String defaultValue) {
        return getDefaultIfNull(text, defaultValue, false);
    }

    public static String getDefaultIfNull(String text, String defaultValue, boolean trim) {
        if (isNull(text)) { return defaultValue; }
        if (trim) { return text.trim(); }

        return text;
    }

    public static String getDefaultIfNullOrEmpty(String text, String defaultValue) {
        return getDefaultIfNullOrEmpty(text, defaultValue, false);
    }

    public static String getDefaultIfNullOrEmpty(String text, String defaultValue, boolean trim) {
        if (isNullOrEmpty(text)) { return defaultValue; }
        if (trim) { return text.trim(); }

        return text;
    }

    public static String getDefaultIfNullOrWhiteSpace(String text, String defaultValue) {
        return getDefaultIfNullOrWhiteSpace(text, defaultValue, false);
    }

    public static String getDefaultIfNullOrWhiteSpace(String text, String defaultValue, boolean trim) {
        if (isNullOrWhiteSpace(text)) { return defaultValue; }
        if (trim) { return text.trim(); }

        return text;
    }

    /**
     * Replaces whitespace characters like tab ('\t'), newline ('\n')
     * and carriage-return ('\r') from the text.
     * @implNote This method does not replace spaces ('\s') and replaces
     * white spaces with a single space ('\s').
     * @param text Text from which white space characters shall be replaced.
     * @return The text with all the white space characters replaced.
     */
    public static String replaceWhiteSpaces(String text) {
        return replaceWhiteSpaces(text, false);
    }

    /**
     * Replaces whitespace characters like tab ('\t'), newline ('\n')
     * and carriage-return ('\r') from the text.
     * @implNote This method replaces white spaces with a single space.
     * @param text Text from which white space characters shall be replaced.
     * @return The text with all the white space characters replaced.
     */
    public static String replaceWhiteSpaces(String text, boolean replaceSpaces) {
        return replaceWhiteSpaces(text, " ", replaceSpaces);
    }

    /**
     * Replaces white space characters like tab ('\t'), newline ('\n')
     * and carriage-return ('\r') from the text.
     * @param text Text from which white space characters shall be replaced.
     * @param replacement The replacement string for newline characters.
     * @param replaceSpaces If true, this method replaces space ('\s') characters.
     * @return The text with all the white space characters replaced.
     */
    public static String replaceWhiteSpaces(String text, String replacement, boolean replaceSpaces) {
        final var regularExpression = replaceSpaces ? "[\s\t\r\n]+" : "[\r\n\t]+";

        return text.replaceAll(regularExpression, replacement);
    }

    /**
     * Replaces newline ('\n') and carriage-return ('\r')
     * characters from the text.
     * @implNote This method uses space (' ') as the replacement
     * character.
     * @param text Text from which the newline characters shall
     *             be replaced.
     * @return The text with all the newline characters replaced.
     */
    public static String replaceNewlines(String text) {
        return replaceNewlines(text, " ");
    }

    /**
     * Replaces newline ('\n') and carriage-return ('\r')
     * characters from the text.
     * @param text Text from which the newline characters shall
     *             be replaced.
     * @param replacement The replacement string for newline characters.
     * @return The text with all the newline characters replaced.
     */
    public static String replaceNewlines(String text, String replacement) {
        return text.replaceAll("[\r\n]+", replacement);
    }

    public static String toCommaSeparatedValue(final boolean escape, final Object... values) {
        return toSeparatedValue(escape, COMMA_SEPARATOR, values);
    }

    public static String toCommaSeparatedCustomWrappedValue(
            final boolean escape,
            final String openingValueWrapper,
            final String closingValueWrapper,
            final Object... values) {
        return toSeparatedValue(escape, openingValueWrapper, closingValueWrapper, COMMA_SEPARATOR, values);
    }

    public static String toSeparatedValue(final String separator, final Object... values) {
        return toSeparatedValue(false, separator, values);
    }

    public static String toSeparatedValue(final boolean escape, final String separator, final Object... values) {
        var openingValueWrapper = StringUtilities.getEmptyString();
        var closingValueWrapper = StringUtilities.getEmptyString();

        // if values shall be escaped...
        if (escape) {
            // we shall use the default opening and closing value wrapper...
            openingValueWrapper = DEFAULT_OPENING_VALUE_WRAPPER;
            closingValueWrapper = DEFAULT_CLOSING_VALUE_WRAPPER;
        }

        return toSeparatedValue(escape, openingValueWrapper, closingValueWrapper, separator, values);
    }

    public static String toSeparatedValue(
            final boolean escape,
            final String openingValueWrapper,
            final String closingValueWrapper,
            final String separator,
            final Object... values) {
        // if no value is provided, we shall return an empty string...
        if (values == null || values.length == 0) { return getEmptyString(); }

        // creating a new string builder...
        final var separatedValueBuilder = new StringBuilder(STRING_BUILDER_INITIAL_CAPACITY);
        // we shall create a new date format...
        // NOTE: NEED TO CREATE NEW DATE FORMAT BECAUSE IT IS GENERALLY NOT THREAD-SAFE...
        final var dateTimeFormat = DateTimeFormatter.createDateFormat();

        // iterating over all the values...
        for (var i = 0; i < values.length; ++i) {
            // selecting each value...
            final var value = values[i];
            // holds the value as string...
            var valueAsString = getEmptyString();

            // if the value is not null...
            if (value != null) {
                // if the value is an instance of date...
                valueAsString = value instanceof Date valueAsDate
                        ? dateTimeFormat.format(valueAsDate)          // <-- we shall format the date value...
                        : value.toString();                           // <-- otherwise, we shall convert the value to string...

                // if the value shall be escaped...
                if (escape) {
                    // replacing the carriage-return ('\r') and new-line ('\n') characters with spaces...
                    valueAsString = replaceNewlines(valueAsString, " ").trim();
                    // replacing the double quotes (") with two double quotes (")...
                    valueAsString = valueAsString.replace("\"", "\"\"");
                }

                // if opening and closing value wrappers are not null nor empty...
                if (!StringUtilities.isNullOrEmpty(openingValueWrapper)
                        && !StringUtilities.isNullOrEmpty(closingValueWrapper)) {
                    // we shall wrap the value...
                    valueAsString = openingValueWrapper + valueAsString + closingValueWrapper;
                }
            }

            // now we shall append the value to the string builder...
            separatedValueBuilder.append(valueAsString);
            // we shall also append the separator...
            separatedValueBuilder.append(separator);
        }

        // removing the separator at the end of the string and getting the value...
        final var separatedValue = separatedValueBuilder
                .substring(0, separatedValueBuilder.length() - separator.length());

        // finally, we shall return the value...
        return separatedValue;
    }

    public static String truncate(String text, int length) {
        // if text is null, we shall return an empty string...
        if (isNull(text)) { return getEmptyString(); }

        // if the given length is greater than or equal to zero (0)
        // and less than the length of the given text...
        if (length > -1 && length < text.length()) {
            // we shall truncate the given text...
            return text.substring(0, length);
        }

        // otherwise, we shall return the text as-is...
        return text;
    }

    public static String[] toStringArray(final Object[] values) {
        return Arrays.stream(values).map(Object::toString).toArray(String[]::new);
    }

    public static String toAlphaNumeric(String text) {
        text = StringUtilities.getDefaultIfNullOrWhiteSpace(
                text, StringUtilities.getEmptyString(), true);

        if (StringUtilities.isEmpty(text)) { return StringUtilities.getEmptyString(); }

        // removing non-alphanumeric characters (if any)...
        text = text.replaceAll(NON_ALPHANUMERIC_CHARACTERS_REGULAR_EXPRESSION, StringUtilities.getEmptyString());

        return text;
    }
}
