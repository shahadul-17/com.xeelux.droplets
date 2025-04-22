package com.xeelux.droplets.core.concurrency;

import java.util.concurrent.locks.Lock;

public final class ThreadSafeExecutor {

    public static <ReturnType> ReturnType execute(
            Lock lock,
            ThreadSafeAction<ReturnType> action) throws RuntimeException {
        ReturnType value;

        lock.lock();                // <-- acquiring the lock...

        try {
            value = action.execute();
        } catch (final RuntimeException exception) {
            // if the exception is a runtime exception, we'll just re-throw...
            throw exception;
        } catch (final Throwable throwable) {
            // any kind of throwable shall be transformed into a runtime exception...
            throw new RuntimeException("An exception occurred while executing the action.", throwable);
        } finally {
            lock.unlock();          // <-- releasing the lock...
        }

        return value;
    }
}
