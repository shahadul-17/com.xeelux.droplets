package com.xeelux.droplets.core.utilities;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class ThreadUtilities {

    private static final Logger logger = LogManager.getLogger(ThreadUtilities.class);

    public static void trySleep(final long timeoutInMilliseconds) {
        try {
            Thread.sleep(timeoutInMilliseconds);
        } catch (final Exception exception) { }
    }

    /**
     * When called from a virtual thread, this method
     * retrieves the name of the platform thread on which
     * the virtual thread is currently mounted. If the thread
     * is not a virtual thread, even then this method returns the
     * platform thread name.
     * @return The platform thread name.
     */
    public static String getCurrentPlatformThreadName() {
        // gets the current thread...
        final var currentThread = Thread.currentThread();

        // if the current thread is not a virtual thread...
        if (!currentThread.isVirtual()) {
            // returns the current thread name...
            return currentThread.getName();
        }

        // otherwise, gets the current thread information in lower case...
        final var currentThreadInformation = currentThread.toString().toLowerCase();
        // finds the index of "worker-"...
        final var indexOfWorker = currentThreadInformation.indexOf("worker-");

        // if "worker-" is not found...
        if (indexOfWorker == -1) {
            // we shall return an empty string...
            return StringUtilities.getEmptyString();
        }

        // extracts the platform thread name from the information...
        final var platformThreadName = currentThreadInformation.substring(indexOfWorker);

        // returns the platform thread name...
        return platformThreadName;
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown request,
     * or the timeout occurs, or the current thread is interrupted, whichever happens first.
     * @param timeoutInMilliseconds The maximum time to wait (in milliseconds).
     * @param executorService Executor service that shall await termination.
     * @return true if the executor terminated or interrupted while waiting
     * and false if the timeout elapsed before termination.
     */
    public static boolean awaitExecutorServiceTermination(
            final long timeoutInMilliseconds,
            final ExecutorService executorService) {
        boolean terminated;

        try {
            terminated = executorService.awaitTermination(
                    timeoutInMilliseconds,
                    TimeUnit.MILLISECONDS);
        } catch (final Exception exception) {
            // in case of exception, returns true...
            return true;
        }

        return terminated;
    }

    public static void tryInterrupt(final Thread thread) {
        // if the thread is 'null', we shall return...
        if (thread == null) { return; }

        try {
            // tries to interrupt the thread...
            thread.interrupt();
        } catch (final Exception exception) {
            logger.log(Level.WARN, "An exception occurred while interrupting thread.", exception);
        }
    }

    public static void tryJoin(final Thread thread) {
        // if the thread is 'null', we shall return...
        if (thread == null) { return; }

        try {
            // tries to wait for the thread to finish...
            thread.join();
        } catch (final Exception exception) {
            logger.log(Level.WARN, "An exception occurred while joining thread.", exception);
        }
    }
}
