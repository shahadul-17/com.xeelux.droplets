package com.xeelux.droplets.core.threading;

import com.xeelux.droplets.core.utilities.ObjectUtilities;
import com.xeelux.droplets.core.utilities.ThreadUtilities;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

final class AsyncTaskExecutor {

    private static final int EXECUTOR_SERVICE_TERMINATION_WAIT_TIMEOUT_IN_MILLISECONDS = 20;
    private static final Logger logger = LogManager.getLogger(AsyncTaskExecutor.class);
    private static final ThreadFactory virtualThreadFactory = Thread.ofVirtual()
            .name("virtual-", 1L)
            .factory();
    private static final ExecutorService executorService = Executors.newThreadPerTaskExecutor(virtualThreadFactory);

    /**
     * This method submits a task to the executor service
     * in a thread-safe manner.
     * @param task Task to execute.
     * @return An AsyncTask object.
     * @param <Type> Asynchronous task result type.
     */
    @SuppressWarnings(value = "unchecked")
    private static <Type> AsyncTask<Type> submitTaskToExecutorService(final Object task) {
        Future<?> future = null;
        Throwable throwable = null;

        try {
            // checks the instance type of the task...
            if (task instanceof Runnable runnable) {
                future = executorService.submit(runnable);
            } else if (task instanceof Callable<?> callable) {
                future = executorService.submit(callable);
            } else {
                // if the task doesn't match any of the types,
                // we'll set an exception...
                throwable = new Exception("Invalid task provided.");
            }
        } catch (final Throwable _throwable) {
            // assigns the exception to the outer scope variable...
            throwable = _throwable;
        }

        // if future is not null, returns an async task derived from the future...
        if (future != null) { return AsyncTask.from((Future<Type>) future); }

        logger.log(Level.ERROR, "An exception occurred while running the async task.", throwable);

        // otherwise, we shall create an async task from the throwable...
        return AsyncTask.from(throwable);
    }

    /**
     * Asynchronously executes a task.
     * @implNote This method is thread-safe.
     * @param task Task to execute.
     * @return An AsyncTask object.
     */
    static AsyncTask<?> run(final Runnable task) {
        return submitTaskToExecutorService(task);
    }

    /**
     * Asynchronously executes a task.
     * @implNote This method is thread-safe.
     * @param task Task to execute.
     * @return An AsyncTask object.
     * @param <Type> Asynchronous task result type.
     */
    static <Type> AsyncTask<Type> run(final Callable<Type> task) {
        return submitTaskToExecutorService(task);
    }

    /**
     * Throws exception if the provided array of objects
     * contains exception.
     * @param objects An array of objects that may or may not
     *                contain exception.
     * @return The provided array without any modification.
     * @throws RuntimeException If any of the objects is an exception.
     */
    private static Object[] throwExceptionIfExists(final Object[] objects) throws RuntimeException {
        for (var i = 0; i < objects.length; ++i) {
            final var object = objects[i];

            // if the object is not an instance of a runtime exception class,
            // we shall skip this iteration...
            if (!(object instanceof RuntimeException exception)) { continue; }

            // if a runtime exception is found, we'll throw the exception...
            throw exception;
        }

        // if no exception is found after iteration,
        // we shall return the objects as-is...
        return objects;
    }

    /**
     * Awaits all the async tasks. This method throws
     * the first available exception (if found).
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * @throws Throwable If any of the async tasks threw exception.
     */
    static Object[] await(final AsyncTask<?>[] asyncTasks) throws RuntimeException {
        // awaits all the async tasks...
        final var results = awaitAll(asyncTasks);

        // otherwise, looks for a throwable object within the list of results...
        return throwExceptionIfExists(results);
    }

    /**
     * Awaits all the async tasks. This method throws
     * the first available exception (if found).
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * @throws Exception If any of the async tasks threw exception.
     */
    static Object[] await(final Iterable<AsyncTask<?>> asyncTasks) throws RuntimeException {
        // awaits all the async tasks...
        final var results = awaitAll(asyncTasks);

        // otherwise, looks for exception object within the results list...
        return throwExceptionIfExists(results);
    }

    /**
     * Awaits all the async tasks. This method throws
     * the first available exception (if found).
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * @throws RuntimeException If any of the async tasks threw exception.
     */
    static Object[] await(final List<AsyncTask<?>> asyncTasks) throws RuntimeException {
        // awaits all the async tasks...
        var results = awaitAll(asyncTasks);

        // otherwise, looks for a throwable object within the list of results...
        return throwExceptionIfExists(results);
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(final AsyncTask<?>[] asyncTasks) {
        return awaitAll(asyncTasks, asyncTasks.length);
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @param length Length of the array till which
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(final AsyncTask<?>[] asyncTasks, int length) {
        // instantiates an array to hold all the async task results...
        final Object[] results = new Object[length];

        for (var i = 0; i < length; ++i) {
            final var asyncTask = asyncTasks[i];
            Object result;

            try {
                // awaiting the async task may throw an exception...
                result = asyncTask.await();
            } catch (final RuntimeException exception) {
                // if an exception is thrown, we shall set the
                // exception as the result...
                result = exception;
            }

            // adds the result to the array...
            results[i] = result;
        }

        // finally, we shall return the results...
        return results;
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(final Iterable<AsyncTask<?>> asyncTasks) {
        // instantiates a list to hold all the async tasks...
        final List<AsyncTask<?>> _asyncTasks = new ArrayList<>();

        // adds all the async tasks to our newly created list...
        for (final var asyncTask : asyncTasks) {
            _asyncTasks.add(asyncTask);
        }

        // awaits all the tasks...
        return awaitAll(_asyncTasks);
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(final List<AsyncTask<?>> asyncTasks) {
        // checks if null or empty list is provided...
        if (asyncTasks == null || asyncTasks.isEmpty()) { return ObjectUtilities.getEmptyObjectArray(); }

        return awaitAll(asyncTasks.toArray(new AsyncTask[0]));
    }

    static ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Releases all the resources associated with the
     * asynchronous task execution runtime.
     */
    static void dispose() {
        logger.log(Level.INFO, "Releasing all the resources associated with the asynchronous task executor.");

        try {
            executorService.shutdownNow();

            logger.log(Level.INFO, "Executor service shutdown successful.");
        } catch (final Throwable throwable) {
            logger.log(Level.ERROR, "An exception occurred while shutting down the underlying executor service.", throwable);
        }

        // waits for the executor service termination...
        while (!ThreadUtilities.awaitExecutorServiceTermination(
                EXECUTOR_SERVICE_TERMINATION_WAIT_TIMEOUT_IN_MILLISECONDS, executorService)) {
            logger.log(Level.INFO, "Waiting for the executor service termination.");
        }

        logger.log(Level.INFO, "Successfully terminated the executor service.");

        try {
            executorService.close();

            logger.log(Level.INFO, "Successfully closed the executor service.");
        } catch (final Throwable throwable) {
            logger.log(Level.WARN, "An exception occurred while closing the underlying executor service.", throwable);
        }
    }
}
