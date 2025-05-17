package com.xeelux.droplets.core.concurrency;

public interface ThreadSafeBlockingQueue<Type> {
    int size();
    void enqueue(final Type element) throws RuntimeException;
    Type dequeue() throws RuntimeException;
    void clear();
}
