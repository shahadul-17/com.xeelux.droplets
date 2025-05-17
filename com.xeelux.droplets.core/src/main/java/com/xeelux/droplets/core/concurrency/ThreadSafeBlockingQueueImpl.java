package com.xeelux.droplets.core.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadSafeBlockingQueueImpl<Type> implements ThreadSafeBlockingQueue<Type> {

    private final BlockingQueue<Type> blockingQueue;

    public ThreadSafeBlockingQueueImpl() {
        blockingQueue = new LinkedBlockingQueue<>();
    }

    public ThreadSafeBlockingQueueImpl(final int capacity) {
        blockingQueue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public int size() {
        return blockingQueue.size();
    }

    @Override
    public void enqueue(final Type element) throws RuntimeException {
        try {
            blockingQueue.put(element);
        } catch (final Throwable exception) {
            throw new RuntimeException("An exception occurred while placing the element to the queue.", exception);
        }
    }

    @Override
    public Type dequeue() throws RuntimeException {
        Type element;

        try {
            element = blockingQueue.take();
        } catch (final Throwable exception) {
            throw new RuntimeException("An exception occurred while removing the first element from the queue.", exception);
        }

        return element;
    }

    @Override
    public void clear() {
        blockingQueue.clear();
    }
}
