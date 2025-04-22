package com.xeelux.droplets.core.text;

import com.xeelux.droplets.core.utilities.CollectionUtilities;
import com.xeelux.droplets.core.utilities.StringUtilities;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class Encoder {

    private static final char[] UPPER_CASED_HEXADECIMAL_SYMBOLS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F',
    };

    private static final char[] LOWER_CASED_HEXADECIMAL_SYMBOLS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f',
    };

    // NOTE: INSTANCES OF Base64.Encoder and Base64.Decoder CLASSES ARE THREAD SAFE...
    // we are saving an instance of regular Base64 encoder which has padding enabled...
    private static final Base64.Encoder paddingEnabledBase64Encoder = Base64.getEncoder();
    // we are saving an instance of URL safe Base64 encoder which has padding enabled...
    private static final Base64.Encoder paddingEnabledUrlSafeBase64Encoder = Base64.getUrlEncoder();
    // we are saving an instance of regular Base64 encoder which has padding disabled...
    private static final Base64.Encoder paddingDisabledBase64Encoder = paddingEnabledBase64Encoder.withoutPadding();
    // we are saving an instance of URL safe Base64 encoder which has padding disabled...
    private static final Base64.Encoder paddingDisabledUrlSafeBase64Encoder = paddingEnabledUrlSafeBase64Encoder.withoutPadding();
    // we are saving an instance of regular Base64 decoder...
    private static final Base64.Decoder base64Decoder = Base64.getDecoder();
    // we are saving an instance of URL safe Base64 decoder...
    private static final Base64.Decoder urlSafeBase64Decoder = Base64.getUrlDecoder();

    public static String toUtf8(byte[] bytes) {
        return toUtf8(bytes, 0, bytes.length);
    }

    public static String toUtf8(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, StandardCharsets.UTF_8);
    }

    public static byte[] fromUtf8(String encodedText) {
        // if provided encoded text is null or empty,
        // we shall return an array of length zero (0)...
        if (StringUtilities.isNullOrEmpty(encodedText)) { return CollectionUtilities.getEmptyByteArray(); }

        // gets byte array...
        return encodedText.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Converts an array of bytes to hex/base16 string.
     * @implNote This algorithm is taken from StackOverflow (answered by Crystark).
     * You may check the following URL for more details.
     * https://stackoverflow.com/questions/2817752/how-can-i-convert-a-byte-array-to-hexadecimal-in-java
     * @param bytes Array of bytes to be converted.
     * @return Lower-cased hex/base16 string.
     */
    public static String toBase16(byte[] bytes) {
        return toBase16(bytes, false);
    }

    /**
     * Converts an array of bytes to hex/base16 string.
     * @implNote This algorithm is taken from StackOverflow (answered by Crystark).
     * You may check the following URL for more details.
     * https://stackoverflow.com/questions/2817752/how-can-i-convert-a-byte-array-to-hexadecimal-in-java
     * @param bytes Array of bytes to be converted.
     * @param upperCased If set to true, the output of the hexadecimal symbols
     *                   shall be upper-cased. Otherwise, the symbols shall be
     *                   lower-cased.
     * @return Hex/Base16 string.
     */
    public static String toBase16(byte[] bytes, boolean upperCased) {
        // selecting hexadecimal symbols based on the flag...
        final var hexadecimalSymbols = upperCased
                ? UPPER_CASED_HEXADECIMAL_SYMBOLS
                : LOWER_CASED_HEXADECIMAL_SYMBOLS;
        // this array of characters shall hold the base-16 representation of the bytes.
        // the length of the array shall be twice the number of bytes...
        final var bytesAsHexadecimalSymbols = new char[bytes.length * 2];

        for (int i = 0, j = 0; i < bytes.length; ++i, j += 2) {
            // selecting byte value at index 'i'...
            final var byteValue = bytes[i];
            final var hexadecimalSymbolA = hexadecimalSymbols[(byteValue & 0xF0) >> 4];
            final var hexadecimalSymbolB = hexadecimalSymbols[byteValue & 0x0F];

            // sets both hexadecimal symbols to the character array...
            bytesAsHexadecimalSymbols[j] = hexadecimalSymbolA;
            bytesAsHexadecimalSymbols[j + 1] = hexadecimalSymbolB;
        }

        // creates a string from the array of characters...
        final var base16Text = new String(bytesAsHexadecimalSymbols);

        // returns hex/base16 string...
        return base16Text;
    }

    /**
     * Gets the numerical value (decimal equivalent) of the hex/base-16 symbol.
     * e.g. '1' = 1, 'A'/'a' = 10, 'F'/'f' = 15.
     * @implNote To make it performant, some checks were not made.
     * So, this method is not very secure due to the lack of some
     * bound checks. Non-hex symbols shall return erroneous values.
     * @param base16Symbol Hex/Base-16 symbol of which the numeric value
     *                     shall be retrieved. e.g. '1', 'A', 'f'.
     * @return The numerical value of the hex/base-16 symbol.
     */
    private static int getValueOfBase16Symbol(char base16Symbol) {
        // ascii value of the base-16 symbol...
        var asciiValue = (int)base16Symbol;

        // if the symbol is less than or equal to '9' (as character),
        // we shall subtract the ascii value of '0' to get the numeric value
        // of the symbol...
        if (asciiValue < 58) { return asciiValue - 48; }        // <-- 48 is the ascii value of '0' and 57 is the ascii value of '9'...
        // if the symbol is less than or equal to 'F', we shall subtract
        // the ascii value of 'A' to get the numeric value of the symbol...
        // NOTE: WE ARE RETURNING (asciiValue - 65 + 10) = (asciiValue - 55)
        // BECAUSE, 65 IS THE ASCII VALUE OF 'A' AND 'A' IN HEXADECIMAL IS
        // EQUIVALENT TO 10 IN DECIMAL NUMBER SYSTEM...
        if (asciiValue < 71) { return asciiValue - 55; }        // <-- 70 is the ascii value of 'F'...

        // otherwise, we shall subtract the ascii value of 'A'
        // to get the numeric value of the symbol...
        // NOTE: WE ARE RETURNING (asciiValue - 97 + 10) = (asciiValue - 87)
        // BECAUSE, 97 IS THE ASCII VALUE OF 'a' AND 'a' IN HEXADECIMAL IS
        // EQUIVALENT TO 10 IN DECIMAL NUMBER SYSTEM...
        return asciiValue - 87;                                 // <-- 97 is the ascii value of 'a'...
    }

    /**
     * Decodes the encoded text.
     * @param encodedText Encoded text to be decoded.
     * @return Returns the decoded content.
     */
    public static byte[] fromBase16(String encodedText) {
        // converts the encoded text into an array of characters...
        final var encodedTextAsCharacters = encodedText.toCharArray();
        // gets the length of the encoded text...
        final var encodedTextLength = encodedTextAsCharacters.length;
        // the length of the byte array shall be half the length
        // of the hex/base16 encoded text...
        final var byteArrayLength = encodedTextLength / 2;
        // initializing a byte array...
        final var bytes = new byte[byteArrayLength];

        for (int i = 0, j = 0; i < encodedTextLength; i += 2, ++j) {
            final var base16SymbolA = encodedTextAsCharacters[i];
            final var base16SymbolB = encodedTextAsCharacters[i + 1];
            final var numericValueA = getValueOfBase16Symbol(base16SymbolA);
            final var numericValueB = getValueOfBase16Symbol(base16SymbolB);
            final var byteValue = (byte)((numericValueA << 4) + numericValueB);

            bytes[j] =  byteValue;
        }

        // returns the decoded bytes...
        return bytes;
    }

    private static Base64.Encoder getBase64Encoder(boolean urlSafe, boolean paddingEnabled) {
        // if URL safe flag is true...
        if (urlSafe) {
            // if padding shall be enabled, we shall return the padding enabled
            // URL safe Base64 encoder...
            if (paddingEnabled) { return paddingEnabledUrlSafeBase64Encoder; }

            // otherwise, we shall return the padding disabled
            // URL safe Base64 encoder...
            return paddingDisabledUrlSafeBase64Encoder;
        }

        // if URL safe flag is false and padding shall be enabled,
        // we shall return the padding enabled regular Base64 encoder...
        if (paddingEnabled) { return paddingEnabledBase64Encoder; }

        // otherwise, we shall return the padding disabled regular Base64 encoder...
        return paddingDisabledBase64Encoder;
    }

    private static Base64.Decoder getBase64Decoder(boolean urlSafe) {
        // if URL safe flag is true, we shall return the URL safe Base64 decoder...
        if (urlSafe) { return urlSafeBase64Decoder; }

        // otherwise, we shall return the regular Base64 decoder...
        return base64Decoder;
    }

    /**
     * Converts an array of bytes to base64 string.
     * @param bytes Array of bytes to be converted.
     * @param urlSafe If set to true, the output of the base64 encoding shall
     *                be URL safe.
     * @param paddingEnabled If set to true, the output of the base64 encoding
     *                   shall contain padding symbols. Otherwise, the symbols
     *                   shall be omitted.
     * @return Base64 string.
     */
    public static String toBase64(byte[] bytes, boolean urlSafe, boolean paddingEnabled) {
        // retrieves the base64 encoder...
        final var base64Encoder = getBase64Encoder(urlSafe, paddingEnabled);
        // encodes the bytes into base64 string...
        final var base64Text = base64Encoder.encodeToString(bytes);

        // returns the base64 text...
        return base64Text;
    }

    /**
     * Converts an array of bytes to regular (not URL-safe) base64 string.
     * @param bytes Array of bytes to be converted.
     * @param paddingEnabled If set to true, the output of the base64 encoding
     *                   shall contain padding symbols. Otherwise, the symbols
     *                   shall be omitted.
     * @return Regular (not URL-safe) base64 string.
     */
    public static String toBase64(byte[] bytes, boolean paddingEnabled) {
        return toBase64(bytes, false, paddingEnabled);
    }

    /**
     * Converts an array of bytes to regular (not URL-safe) base64 string
     * with padding enabled.
     * @param bytes Array of bytes to be converted.
     * @return Regular (not URL-safe) base64 string.
     */
    public static String toBase64(byte[] bytes) {
        return toBase64(bytes, true);
    }

    /**
     * Converts an array of bytes to URL-safe base64 string.
     * @param bytes Array of bytes to be converted.
     * @param paddingEnabled If set to true, the output of the base64 encoding
     *                   shall contain padding symbols. Otherwise, the symbols
     *                   shall be omitted.
     * @return URL-safe base64 string.
     */
    public static String toUrlSafeBase64(byte[] bytes, boolean paddingEnabled) {
        return toBase64(bytes, true, paddingEnabled);
    }

    /**
     * Converts an array of bytes to URL-safe base64 string
     * with padding enabled.
     * @param bytes Array of bytes to be converted.
     * @return URL-safe base64 string.
     */
    public static String toUrlSafeBase64(byte[] bytes) {
        return toUrlSafeBase64(bytes, true);
    }

    /**
     * Decodes the base64 encoded text.
     * @param encodedText Base64 encoded text to be decoded.
     * @param urlSafe If set to true, the encoded base64 text
     *                will be considered as URL safe.
     * @return Returns the decoded content.
     */
    public static byte[] fromBase64(String encodedText, boolean urlSafe) {
        // retrieves the base64 decoder...
        final var base64Encoder = getBase64Decoder(urlSafe);
        // decodes the base64 encoded text into bytes...
        final var bytes = base64Encoder.decode(encodedText);

        // returns the decoded bytes...
        return bytes;
    }

    /**
     * Decodes the regular (not URL-safe) base64 encoded text.
     * @param encodedText Regular (not URL-safe) base64 encoded text to be decoded.
     * @return Returns the decoded content.
     */
    public static byte[] fromBase64(String encodedText) {
        return fromBase64(encodedText, false);
    }

    /**
     * Decodes the URL-safe base64 encoded text.
     * @param encodedText URL-safe base64 encoded text to be decoded.
     * @return Returns the decoded content.
     */
    public static byte[] fromUrlSafeBase64(String encodedText) {
        return fromBase64(encodedText, true);
    }

    /**
     * Encodes the given bytes into the specified encoding.
     * @implNote Uses the default options for the specified
     * encoding mechanism.
     * @param bytes Array of bytes to be encoded.
     * @param encoding Encoding to be used.
     * @return The encoded text.
     */
    public static String encode(byte[] bytes, Encoding encoding) {
        // encodes the bytes using the encoding provided...
        var encodedText = switch (encoding) {
            case UTF_8 -> toUtf8(bytes);
            case HEX, BASE_16 -> toBase16(bytes);
            case BASE_64 -> toBase64(bytes);
            case URL_SAFE_BASE_64 -> toUrlSafeBase64(bytes);
            default -> StringUtilities.getEmptyString();
        };

        // returns the encoded text...
        return encodedText;
    }

    /**
     * Decodes the encoded text into bytes using the specified encoding.
     * @param encodedText Text to be decoded.
     * @param encoding Encoding that was used to encode the text.
     * @return Returns the decoded content.
     */
    public static byte[] decode(String encodedText, Encoding encoding) {
        // decodes the encoded text using the encoding provided...
        var bytes = switch (encoding) {
            case UTF_8 -> fromUtf8(encodedText);
            case HEX, BASE_16 -> fromBase16(encodedText);
            case BASE_64 -> fromBase64(encodedText);
            case URL_SAFE_BASE_64 -> fromUrlSafeBase64(encodedText);
            default -> CollectionUtilities.getEmptyByteArray();
        };

        // returns the decoded content as bytes...
        return bytes;
    }
}
