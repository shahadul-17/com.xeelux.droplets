package com.xeelux.droplets.core.concurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ThreadSafeBoolean {

    private volatile boolean value = false;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public ThreadSafeBoolean() { }

    public ThreadSafeBoolean(boolean initialValue) {
        value = initialValue;
    }

    public boolean getUnsafe() {
        return value;
    }

    public boolean get() {
        return ThreadSafeExecutor.execute(readLock, this::getUnsafe);
    }

    public void setUnsafe(boolean value) {
        this.value = value;
    }

    public void set(boolean value) {
        setAndGet(value);
    }

    public boolean getAndSet(boolean value) {
        return ThreadSafeExecutor.execute(writeLock, () -> {
            // storing the previous value into a temporary variable...
            final var previousValue = this.value;
            // assigning the new value...
            setUnsafe(value);

            return previousValue;
        });
    }

    public boolean setAndGet(boolean value) {
        return ThreadSafeExecutor.execute(writeLock, () -> {
            // assigning the new value...
            setUnsafe(value);

            return this.value;
        });
    }
}
