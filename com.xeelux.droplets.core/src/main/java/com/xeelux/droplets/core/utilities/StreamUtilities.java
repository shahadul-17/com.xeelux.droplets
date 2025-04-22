package com.xeelux.droplets.core.utilities;

import com.xeelux.droplets.core.text.Encoder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;

public final class StreamUtilities {

    private static final Logger logger = LogManager.getLogger(StreamUtilities.class);
    private static final int BUFFER_LENGTH = 8192;
    private static final int STRING_BUILDER_INITIAL_CAPACITY = 8192;

    /**
     * Reads bytes into a portion of an array.
     * This method will block until some input is available,
     * an I/O error occurs, or the end of the stream is reached.
     * @param buffer Destination buffer.
     * @param offset Offset at which to start storing bytes.
     * @param length Maximum number of bytes to read.
     * @param inputStream Input stream from which to start reading.
     * @return The number of bytes read. Returns -1
     * if end of stream is reached. Returns -2 in case of exception.
     */
    public static int read(final byte[] buffer, final int offset, final int length, final InputStream inputStream) {
        return read(buffer, offset, length, inputStream, true);
    }

    /**
     * Reads bytes into a portion of an array.
     * This method will block until some input is available,
     * an I/O error occurs, or the end of the stream is reached.
     * @param buffer Destination buffer.
     * @param offset Offset at which to start storing bytes.
     * @param length Maximum number of bytes to read.
     * @param inputStream Input stream from which to start reading.
     * @param logsEnabled Flag to determine if logs are enabled.
     * @return The number of bytes read. Returns -1
     * if end of stream is reached. Returns -2 in case of exception.
     */
    public static int read(final byte[] buffer, final int offset, final int length,
                           final InputStream inputStream, final boolean logsEnabled) {
        try {
            return inputStream.read(buffer, offset, length);
        } catch (final SocketTimeoutException exception) {
            if (logsEnabled) {
                logger.log(Level.ERROR, "Timeout exception occurred while reading from the input stream.", exception);
            }

            return -3;
        } catch (final Exception exception) {
            if (logsEnabled) {
                logger.log(Level.ERROR, "An exception occurred while reading from the input stream.", exception);
            }

            return -2;
        }
    }

    /**
     * Reads data from the input stream as string.
     * @implNote The input stream is closed after reading completes
     * or exception occurs.
     * @param inputStream Input stream to read from.
     * @return The string data read from the input stream.
     */
    public static String readString(final InputStream inputStream) {
        return readString(inputStream, true);
    }

    /**
     * Reads data from the input stream as string.
     * @param inputStream Input stream to read from.
     * @param closeAutomatically Setting this flag to true shall close
     *                           the input stream after reading or exception.
     * @return The string data read from the input stream.
     */
    public static String readString(
            final InputStream inputStream,
            final boolean closeAutomatically) {
        // if the input stream is null...
        if (inputStream == null) {
            logger.log(Level.WARN, "Provided input stream is 'null'.");

            // we shall return an empty string...
            return StringUtilities.getEmptyString();
        }

        final var buffer = new byte[BUFFER_LENGTH];
        var bytesRead = 0;
        final var contentBuilder = new StringBuilder(STRING_BUILDER_INITIAL_CAPACITY);

        // NOTE: IF BYTES READ IS EQUAL TO -2, IT MEANS EXCEPTION HAS OCCURRED.
        // THUS, THIS LOOP SHALL BE BROKEN...
        while ((bytesRead = read(buffer, 0, buffer.length, inputStream)) > 0) {
            final var content = Encoder.toUtf8(buffer, 0, bytesRead);

            contentBuilder.append(content);
        }

        // if 'closeAutomatically' flag is true,
        // we shall try to close the input stream...
        if (closeAutomatically) { CloseableUtilities.tryClose(inputStream); }
        // returns empty string in case of exception...
        if (bytesRead < -1) { return StringUtilities.getEmptyString(); }

        return contentBuilder.toString().trim();
    }

    /**
     * Reads data from the file.
     * @param filePath Path of the file.
     * @return The entire file content.
     */
    public static String readString(String filePath) {
        // trying to create input stream for the specified file path...
        final var inputStream = tryCreateInputStream(filePath);
        // reading all the contents of the file...
        final var content = readString(inputStream);

        return content;
    }

    public static InputStream createInputStream(String filePath) throws Exception {
        // first, we shall retrieve the absolute file path...
        final var absoluteFilePath = FileSystemUtilities.getAbsolutePath(filePath);
        // otherwise, we shall create an instance of file...
        final var file = new File(absoluteFilePath);
        // then we shall create a file input stream...
        final InputStream inputStream = new FileInputStream(file);

        // lastly, we shall return the input stream...
        return inputStream;
    }

    public static InputStream tryCreateInputStream(String filePath) {
        InputStream inputStream = null;

        try {
            // trying to create input stream...
            inputStream = createInputStream(filePath);
        } catch (Exception exception) {
            logger.log(Level.ERROR, "An exception occurred while creating input stream for file path '" + filePath + "'.", exception);
        }

        // returning the input stream if creation succeeds.
        // otherwise returning null...
        return inputStream;
    }
}
