package com.xeelux.droplets.core.utilities;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;

public final class CloseableUtilities {

    private static final Logger logger = LogManager.getLogger(CloseableUtilities.class);

    /**
     * Tries to close the closeable.
     * @param closeable Closeable to be closed.
     */
    public static void tryClose(final Closeable closeable) {
        if (closeable == null) { return; }

        try {
            closeable.close();
        } catch (final Exception exception) {
            logger.log(Level.WARN, "An exception occurred while closing the closeable.", exception);
        }
    }
}
