package com.xeelux.droplets.core.concurrency;

public interface ThreadSafeAction<ReturnType> {
    ReturnType execute() throws Throwable;
}
