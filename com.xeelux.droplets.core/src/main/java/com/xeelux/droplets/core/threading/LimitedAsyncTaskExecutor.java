package com.xeelux.droplets.core.threading;

import com.xeelux.droplets.core.utilities.SemaphoreUtilities;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class makes sure that a limited number of
 * asynchronous tasks can execute in parallel.
 * @implNote If limit is set to 0, no limit is applied.
 * Calling the no-argument constructor also sets the limit
 * to 0.
 */
public class LimitedAsyncTaskExecutor {

    private final Logger logger = LogManager.getLogger(LimitedAsyncTaskExecutor.class);
    private final int defaultLimit;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);      // <-- this lock is used for thread synchronization...
    // NOTE: READ LOCK CAN BE ACQUIRED BY MULTIPLE THREADS SIMULTANEOUSLY
    // WHEN NO OTHER THREAD HAS ACQUIRED THE WRITE LOCK...
    private final Lock readLock = readWriteLock.readLock();
    // NOTE: WRITE LOCK CAN ONLY BE ACQUIRED BY A SINGLE THREAD...
    private final Lock writeLock = readWriteLock.writeLock();
    private final Map<String, Integer> limitMapByContext;
    // NOTE: THIS MAP CONTAINS SEMAPHORE AS VALUE AND CONTEXT AS KEY...
    private final Map<String, EnhancedSemaphore> semaphoreMapByContext
            = new HashMap<>(SEMAPHORE_MAP_BY_CONTEXT_INITIAL_CAPACITY);

    private static final int SEMAPHORE_MAP_BY_CONTEXT_INITIAL_CAPACITY = 16;
    // NOTE: THIS IS A GARBAGE VALUE...
    private static final String DEFAULT_CONTEXT = "4@42bdb8520c97f!f9b#f5b60V5705a5ad13d0W6";

    public LimitedAsyncTaskExecutor() {
        // setting the default limit to 0 by calling the other constructor...
        this(0);
    }

    public LimitedAsyncTaskExecutor(final int defaultLimit) {
        this(defaultLimit, null);
    }

    public LimitedAsyncTaskExecutor(final int defaultLimit, final Map<String, Integer> limitMapByContext) {
        // applying the sanitized default limit...
        this.defaultLimit = sanitizeLimit(defaultLimit);
        // setting the limit map by context...
        this.limitMapByContext = limitMapByContext;

        // if map is not provided, we shall not proceed any further...
        if (limitMapByContext == null) { return; }

        // otherwise, we shall retrieve all the entries of the map...
        final var entrySet = limitMapByContext.entrySet();

        // and initialize our map...
        for (final var entry : entrySet) {
            // retrieving the context...
            final var context = entry.getKey();
            // retrieving and sanitizing the limit to be applied for the context...
            final var sanitizedLimit = sanitizeLimit(entry.getValue());

            // if limit is zero (0), we shall skip this iteration...
            // NOTE: IF sanitizedLimit IS ZERO (0), WE ARE NOT CREATING THE SEMAPHORE
            // BEFOREHAND SO THAT IT GETS CREATED LATER UTILIZING THE DEFAULT LIMIT...
            if (sanitizedLimit == 0) { continue; }

            // then we shall create a new semaphore for the context...
            final var semaphore = new EnhancedSemaphore(sanitizedLimit, false);

            // and finally we shall put the semaphore to the map...
            // NOTE: THREAD SYNCHRONIZATION IS NOT NEEDED BECAUSE
            // THIS OPERATION IS BEING PERFORMED IN THE CONSTRUCTOR...
            semaphoreMapByContext.put(context, semaphore);
        }
    }

    /**
     * Retrieves semaphore by context.
     * @implNote This method provides synchronous access to semaphore.
     * @param context Context for which semaphore shall be retrieved.
     * @return The semaphore.
     */
    private EnhancedSemaphore retrieveSemaphore(final String context) {
        EnhancedSemaphore semaphore;

        readLock.lock();        // <-- synchronized read starts...

        // gets the semaphore by context...
        semaphore = semaphoreMapByContext.get(context);

        readLock.unlock();      // <-- synchronized read ends...

        // if semaphore is found, we'll return the semaphore...
        if (semaphore != null) { return semaphore; }

        writeLock.lock();       // <-- synchronized write starts...

        // we shall try to get the semaphore by context once again...
        // NOTE: WITHIN THE WRITE LOCK, WE MUST FIRST CHECK IF ANY OTHER THREAD HAS ALREADY CREATED A SEMAPHORE...
        semaphore = semaphoreMapByContext.get(context);

        // if semaphore is not found...
        // NOTE: IF LIMIT IS EQUAL TO ZERO (0), WE SHALL NOT
        // CREATE NEW SEMAPHORE. INSTEAD, WE SHALL RETURN NULL...
        if (defaultLimit > 0 && semaphore == null) {
            logger.log(Level.INFO, "Creating a new semaphore because no semaphore found for the given context, \"{}\".", context);

            // we shall create a new semaphore for the context...
            semaphore = new EnhancedSemaphore(defaultLimit, false);

            // put the newly created semaphore to the map...
            semaphoreMapByContext.put(context, semaphore);
        }

        writeLock.unlock();      // <-- synchronized write ends...

        // return the semaphore...
        return semaphore;
    }

    public int getDefaultLimit() {
        return defaultLimit;
    }

    public int getLimit(final String context) {
        if (context == null || limitMapByContext == null) { return getDefaultLimit(); }

        return limitMapByContext.getOrDefault(context, getDefaultLimit());
    }

    /**
     * Asynchronously executes a task with the applied limit
     * on the default execution context.
     * @implNote This method is thread-safe.
     * @param task Task to execute.
     * @return An AsyncTask object.
     */
    public AsyncTask<?> run(final Runnable task) {
        return run(DEFAULT_CONTEXT, task);
    }

    /**
     * Asynchronously executes a task with the applied limit
     * on the provided execution context.
     * @implNote This method is thread-safe.
     * @param context Task execution context.
     * @param task Task to execute.
     * @return An AsyncTask object.
     */
    public AsyncTask<?> run(final String context, final Runnable task) {
        return run(context, () -> {
            task.run();

            return null;
        });
    }

    /**
     * Asynchronously executes a task with the applied limit
     * on the default execution context.
     * @implNote This method is thread-safe.
     * @param task Task to execute.
     * @return An AsyncTask object.
     */
    public <Type> AsyncTask<Type> run(final Callable<Type> task) {
        return run(DEFAULT_CONTEXT, task);
    }

    /**
     * Asynchronously executes a task with the applied limit
     * on the provided execution context.
     * @implNote This method is thread-safe.
     * @param context Task execution context.
     * @param task Task to execute.
     * @return An AsyncTask object.
     */
    public <Type> AsyncTask<Type> run(final String context, final Callable<Type> task) {
        return AsyncTask.run(() -> {
            // retrieve semaphore by the provided task execution context...
            final var semaphore = retrieveSemaphore(context);

            // if semaphore is null, it means the limit is zero...
            if (semaphore == null) {
                // so we shall execute the task as usual...
                return task.call();
            }

            // tries to acquire the semaphore...
            final var acquired = SemaphoreUtilities.tryAcquireSemaphore(semaphore);

            // if acquiring semaphore fails, we'll return null...
            if (!acquired) { return null; }

            Type result;

            try {
                // but if acquiring semaphore succeeds, we shall execute the task...
                result = task.call();
            } finally {
                // finally, releases the semaphore...
                // WARNING: UNNECESSARILY CALLING RELEASE ON SEMAPHORE
                // MIGHT CAUSE UNEXPECTED BEHAVIOR...!!!
                SemaphoreUtilities.releaseSemaphore(semaphore);
            }

            // returns the result...
            return result;
        });
    }

    private static int sanitizeLimit(final int limit) {
        // if the limit is less than or equal to zero (0), we shall return zero (0).
        // otherwise, we shall return the limit...
        return limit < 1 ? 0 : limit;
    }
}
