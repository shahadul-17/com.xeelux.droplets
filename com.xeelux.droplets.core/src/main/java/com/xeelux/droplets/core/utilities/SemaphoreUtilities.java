package com.xeelux.droplets.core.utilities;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Semaphore;

public final class SemaphoreUtilities {

    private static final Logger logger = LogManager.getLogger(SemaphoreUtilities.class);

    public static boolean tryAcquireSemaphore(final Semaphore semaphore) {
        if (semaphore == null) {
            logger.log(Level.WARN, "Could not acquire the semaphore because it is null.");

            return false;
        }

        try {
            semaphore.acquire();
        } catch (Exception exception) {
            logger.log(Level.WARN, "An exception occurred while acquiring the semaphore.", exception);

            return false;
        }

        // logger.log(Level.DEBUG, "Successfully acquired the semaphore.");

        return true;
    }

    public static void releaseSemaphore(final Semaphore semaphore) {
        if (semaphore == null) {
            logger.log(Level.WARN, "Could not release the semaphore because it is null.");

            return;
        }

        semaphore.release();

        // logger.log(Level.DEBUG, "Successfully released the semaphore.");
    }
}
