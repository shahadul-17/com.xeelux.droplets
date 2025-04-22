package com.xeelux.droplets.core.threading;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Future;

class AsyncTaskImpl<Type> implements AsyncTask<Type> {

    private final Logger logger = LogManager.getLogger(AsyncTaskImpl.class);
    private final Type data;
    private final Future<Type> future;
    private final Throwable throwable;

    private AsyncTaskImpl() {
        this(null, null, null);
    }

    private AsyncTaskImpl(final Type data) {
        this(data, null, null);
    }

    private AsyncTaskImpl(final Future<Type> future) {
        this(null, future, null);
    }

    private AsyncTaskImpl(final Throwable throwable) {
        this(null, null, throwable);
    }

    private AsyncTaskImpl(final Type data, final Future<Type> future, final Throwable throwable) {
        this.data = data;
        this.future = future;
        this.throwable = throwable;
    }

    @Override
    public Type getData() {
        return data;
    }

    @Override
    public Future<Type> getFuture() {
        return future;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public Type await() throws RuntimeException {
        // returns the data...
        if (data != null) { return data; }
        // throws throwable if the throwable is a runtime exception...
        if (throwable instanceof RuntimeException runtimeException) { throw runtimeException; }
        // throws throwable if the throwable is not null...
        if (throwable != null) { throw new RuntimeException(throwable.getMessage(), throwable); }
        // if future is null, we shall return null...
        if (future == null) { return null; }

        Type result;

        try {
            // waits for the future to complete and
            // retrieves the result...
            result = future.get();
        } catch (final RuntimeException exception) {
            logger.log(Level.ERROR, "A runtime exception occurred while awaiting the task.", exception);

            throw exception;
        } catch (final Throwable throwable) {
            logger.log(Level.ERROR, "An exception occurred while awaiting the task.", throwable);

            throw new RuntimeException(throwable.getMessage(), throwable);
        }

        return result;
    }

    @Override
    public Type tryAwait() {
        Type result;

        try {
            result = await();
        } catch (final Throwable throwable) {
            // if an exception is encountered, returns null...
            return null;
        }

        return result;
    }

    static <Type> AsyncTask<Type> from(final Type data) {
        return new AsyncTaskImpl<>(data);
    }

    static <Type> AsyncTask<Type> from(final Future<Type> future) {
        return new AsyncTaskImpl<>(future);
    }

    static <Type> AsyncTask<Type> from(final Throwable throwable) {
        return new AsyncTaskImpl<>(throwable);
    }

    static <Type> AsyncTask<Type> empty() { return new AsyncTaskImpl<>(); }
}
