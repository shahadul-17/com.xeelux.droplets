package com.xeelux.droplets.core.utilities;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Reader;

public final class ReaderUtilities {

    private static final Logger logger = LogManager.getLogger(ReaderUtilities.class);
    private static final int BUFFER_LENGTH = 8192;
    private static final int STRING_BUILDER_INITIAL_CAPACITY = 8192;

    /**
     * Reads characters into a portion of an array.
     * This method will block until some input is available,
     * an I/O error occurs, or the end of the stream is reached.
     * @param buffer Destination buffer.
     * @param offset Offset at which to start storing characters.
     * @param length Maximum number of characters to read.
     * @param reader Reader from which to start reading.
     * @return The number of characters read. Returns -1
     * if end of stream is reached. Returns -2 in case of exception.
     */
    public static int read(final char[] buffer, final int offset, final int length, final Reader reader) {
        try {
            return reader.read(buffer, offset, length);
        } catch (Exception exception) {
            logger.log(Level.ERROR, "An exception occurred while reading from the reader.", exception);

            return -2;
        }
    }

    /**
     * Reads data from the reader as string.
     * @implNote The reader is closed after reading completes
     * or exception occurs.
     * @param reader Reader to read from.
     * @return The string data read from the reader.
     */
    public static String readString(final Reader reader) {
        return readString(reader, true);
    }

    /**
     * Reads data from the reader as string.
     * @param reader Reader to read from.
     * @param closeAutomatically Setting this flag to true shall
     *                           close the reader after reading or exception.
     * @return The string data read from the reader.
     */
    public static String readString(final Reader reader, final boolean closeAutomatically) {
        // if reader is null, we shall return empty string...
        if (reader == null) { return StringUtilities.getEmptyString(); }

        final var buffer = new char[BUFFER_LENGTH];
        var bytesRead = 0;
        final var contentBuilder = new StringBuilder(STRING_BUILDER_INITIAL_CAPACITY);

        // NOTE: IF BYTES READ IS EQUAL TO -2, IT MEANS EXCEPTION HAS OCCURRED.
        // THUS, THIS LOOP SHALL BE BROKEN...
        while ((bytesRead = read(buffer, 0, buffer.length, reader)) > 0) {
            contentBuilder.append(buffer, 0, bytesRead);
        }

        // if 'closeAutomatically' flag is true,
        // we shall try to close the reader...
        if (closeAutomatically) { CloseableUtilities.tryClose(reader); }
        // returns empty string in case of exception...
        if (bytesRead == -2) { return StringUtilities.getEmptyString(); }

        return contentBuilder.toString().trim();
    }
}
