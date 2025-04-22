package com.xeelux.droplets.core.threading;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public interface AsyncTask<Type> {

    /**
     * Gets the data associated with the async task.
     * @return The data associated with the async task.
     * If no data is associated with the task, null is returned.
     */
    Type getData();

    /**
     * Gets the future associated with the async task.
     * @return The future associated with the async task.
     * If no future is associated with the task, null is returned.
     */
    Future<Type> getFuture();

    /**
     * Gets the throwable associated with the async task.
     * @return The throwable associated with the async task.
     * If no throwable is associated with the task, null is returned.
     */
    Throwable getThrowable();

    /**
     * Awaits the async task to finish.
     * @return After the task is finished, returns the result.
     * @throws RuntimeException Exception is thrown if exception occurs
     * while executing the task.
     */
    Type await() throws RuntimeException;

    /**
     * Awaits the async task to finish without throwing any exception.
     * @return After the task is finished, returns the result.
     * If there's an exception while executing the task, returns null.
     */
    Type tryAwait();

    /**
     * This method wraps the provided data within the async task.
     * @param data Data that needs to be wrapped.
     * @return An AsyncTask object.
     * @param <Type> Type of the data.
     */
    static <Type> AsyncTask<Type> from(final Type data) {
        return AsyncTaskImpl.from(data);
    }

    /**
     * This method wraps the provided future within the async task.
     * @param future Future that needs to be wrapped.
     * @return An AsyncTask object.
     * @param <Type> Type of the future.
     */
    static <Type> AsyncTask<Type> from(final Future<Type> future) {
        return AsyncTaskImpl.from(future);
    }

    /**
     * This method wraps the provided throwable within the async task.
     * @param throwable Throwable that needs to be wrapped.
     * @return An AsyncTask object.
     * @param <Type> Type of the future.
     */
    static <Type> AsyncTask<Type> from(final Throwable throwable) {
        return AsyncTaskImpl.from(throwable);
    }

    /**
     * This method returns an empty async task awaiting which returns null.
     * @return An async tasks
     * @param <Type> Type of the data expected after the async task is resolved.
     */
    static <Type> AsyncTask<Type> empty() { return AsyncTaskImpl.empty(); }

    /**
     * Asynchronously executes a task.
     * @implNote This method is thread-safe.
     * @param task Task to execute.
     * @return An AsyncTask object.
     */
    static AsyncTask<?> run(final Runnable task) {
        return AsyncTaskExecutor.run(task);
    }

    /**
     * Asynchronously executes a task.
     * @implNote This method is thread-safe.
     * @param task Task to execute.
     * @return An AsyncTask object.
     * @param <Type> Asynchronous task result type.
     */
    static <Type> AsyncTask<Type> run(final Callable<Type> task) {
        return AsyncTaskExecutor.run(task);
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(final AsyncTask<?>[] asyncTasks) {
        return AsyncTaskExecutor.awaitAll(asyncTasks);
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @param length Length of the array.
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(final AsyncTask<?>[] asyncTasks, final int length) {
        return AsyncTaskExecutor.awaitAll(asyncTasks, length);
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(final Iterable<AsyncTask<?>> asyncTasks) {
        return AsyncTaskExecutor.awaitAll(asyncTasks);
    }

    /**
     * Awaits all the async tasks.
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * The list may contain actual results or exceptions.
     */
    static Object[] awaitAll(final List<AsyncTask<?>> asyncTasks) {
        return AsyncTaskExecutor.awaitAll(asyncTasks);
    }

    /**
     * Awaits all the async tasks. This method throws
     * the first available exception (if found).
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * @throws RuntimeException If any of the async tasks threw exception.
     */
    static Object[] await(final AsyncTask<?>[] asyncTasks) throws RuntimeException {
        return AsyncTaskExecutor.await(asyncTasks);
    }

    /**
     * Awaits all the async tasks. This method throws
     * the first available exception (if found).
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * @throws RuntimeException If any of the async tasks threw exception.
     */
    static Object[] await(final Iterable<AsyncTask<?>> asyncTasks) throws RuntimeException {
        return AsyncTaskExecutor.await(asyncTasks);
    }

    /**
     * Awaits all the async tasks. This method throws
     * the first available exception (if found).
     * @param asyncTasks Async tasks to be awaited.
     * @return An array containing the results of all the tasks.
     * @throws RuntimeException If any of the async tasks threw exception.
     */
    static Object[] await(final List<AsyncTask<?>> asyncTasks) throws RuntimeException {
        return AsyncTaskExecutor.await(asyncTasks);
    }

    /**
     * Retrieves the asynchronous runtime executor service.
     * @return The executor service responsible for the
     * asynchronous tasks.
     */
    static ExecutorService getExecutorService() {
        return AsyncTaskExecutor.getExecutorService();
    }

    /**
     * Releases all the resources associated with the
     * asynchronous task execution runtime.
     */
    static void dispose() {
        AsyncTaskExecutor.dispose();
    }
}
