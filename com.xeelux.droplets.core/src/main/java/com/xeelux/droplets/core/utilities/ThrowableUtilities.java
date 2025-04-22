package com.xeelux.droplets.core.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public final class ThrowableUtilities {

    private static final int STRING_WRITER_INITIAL_CAPACITY = 8192;

    /**
     * Retrieves the stack trace as string from the throwable.
     * @param throwable Throwable, from which the stack trace shall
     *                  be retrieved.
     * @return The stack trace.
     */
    public static String retrieveStackTrace(final Throwable throwable) {
        // initializing a string writer...
        final Writer writer = new StringWriter(STRING_WRITER_INITIAL_CAPACITY);
        // preparing a print writer...
        final var printWriter = new PrintWriter(writer);
        // throwable prints the stack trace to the print writer...
        throwable.printStackTrace(printWriter);
        // we'll flush the print writer...
        printWriter.flush();
        // retrieves the print writer from the string writer...
        final var stackTrace = writer.toString();
        // finally, we must close the print writer...
        printWriter.close();

        // returns the stack trace...
        return stackTrace;
    }
}
