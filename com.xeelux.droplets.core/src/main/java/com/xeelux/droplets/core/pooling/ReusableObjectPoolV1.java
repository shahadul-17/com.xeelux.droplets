package com.xeelux.droplets.core.pooling;

import com.xeelux.droplets.core.concurrency.ThreadSafeExecutor;
import com.xeelux.droplets.core.utilities.ThreadUtilities;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ReusableObjectPoolV1 implements ReusableObjectPool {

    private final Logger logger = LogManager.getLogger(ReusableObjectPoolV1.class);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final int capacity;
    private int threadSleepTimeoutInMilliseconds;
    private Lock lock;
    private Map<String, Object> options;
    private final ReusableObjectInstantiator instantiator;
    private ReusableObject[] reusableObjects;

    private static final int DEFAULT_CAPACITY = 10;
    private static final int DEFAULT_THREAD_SLEEP_TIMEOUT_IN_MILLISECONDS = 100;
    private static final String TEXT = "Reusable Object Pool (Version 1)";

    ReusableObjectPoolV1(final ReusableObjectInstantiator instantiator) {
        this(null, instantiator);
    }

    ReusableObjectPoolV1(
            final Map<String, Object> options,
            final ReusableObjectInstantiator instantiator) {
        // if instantiator is not provided, we shall throw an exception...
        if (instantiator == null) { throw new IllegalArgumentException("Reusable object instantiator must be provided."); }

        // assigning the options...
        setOptions(options);

        // retrieving the capacity...
        capacity = (int) this.options.getOrDefault("capacity", DEFAULT_CAPACITY);
        // assigning the instantiator...
        this.instantiator = instantiator;

        // then we shall call the initialize() method to initialize the pool...
        initialize();
    }

    protected void initialize() {
        // retrieving the capacity...
        final var capacity = getCapacity();
        // retrieving thread sleep timeout...
        threadSleepTimeoutInMilliseconds = (int) this.options.getOrDefault(
                "threadSleepTimeoutInMilliseconds", DEFAULT_THREAD_SLEEP_TIMEOUT_IN_MILLISECONDS);
        // initializing lock...
        lock = new ReentrantLock(false);
        // initializing the array to hold the reusable objects...
        reusableObjects = new ReusableObject[capacity];

        // retrieving the instantiator...
        // NOTE: INSTANTIATOR CAN NOT BE NULL...!!!
        final var instantiator = getInstantiator();

        // instantiating the reusable objects...
        for (var i = 0; i < reusableObjects.length; ++i) {
            // instantiating reusable object...
            final var reusableObject = instantiator.instantiate(i, this.options, this);
            // and placing them into the array...
            reusableObjects[i] = reusableObject;
        }
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }

    protected void setClosed(final boolean closed) {
        this.closed.getAndSet(closed);
    }

    protected boolean setClosedIfNotClosed() {
        // setting true if current value is false.
        // if new value is not set, this method shall return false. otherwise, it shall return true.
        return !this.closed.compareAndSet(false, true);
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public Map<String, Object> getOptions() {
        return options;
    }

    @Override
    public void setOptions(final Map<String, Object> options) {
        // assigning the options...
        this.options = options == null ? Collections.emptyMap() : options;
    }

    protected ReusableObjectInstantiator getInstantiator() {
        return instantiator;
    }

    protected ReusableObject retrieveReusableObject() {
        // NOTE: WE COULD'VE AVOIDED THE USAGE OF LOCK.
        // BUT THAT WOULD'VE UNNECESSARILY CONSUMED MORE CPU...!!!
        return ThreadSafeExecutor.execute(lock, () -> {
            var i = 0;

            while (true) {
                // selecting a reusable object from the array...
                final var reusableObject = reusableObjects[i];

                // if the object is null, we shall skip this iteration...
                if (reusableObject == null) { continue; }

                // otherwise, we shall try to mark this reusable object as unavailable
                // if and only if the object is currently available...
                final var marked = reusableObject.markUnavailableIfAvailable();

                // if the object is successfully marked as unavailable, we shall return the reusable object...
                if (marked) { return reusableObject; }

                ++i;        // <-- incrementing the index...

                // if index is less than the length of the array,
                // we shall continue to the next iteration...
                if (i < reusableObjects.length) { continue; }

                // otherwise, we shall reset the index...
                i = 0;

                // and put the thread to sleep for a while...
                ThreadUtilities.trySleep(threadSleepTimeoutInMilliseconds);
            }
        });
    }

    @Override
    public ReusableObject getReusableObject() {
        // if the pool is closed, we shall throw an exception...
        if (isClosed()) { throw new RuntimeException("Calling getReusableObject() method on closed pool."); }

        // finally, we shall retrieve (in a thread safe manner) and return the reusable object...
        return retrieveReusableObject();
    }

    @Override
    public String toString() {
        return TEXT;
    }

    protected boolean dispose() {
        // if the array of reusable objects is null...
        if (reusableObjects == null) {
            logger.log(Level.INFO, "Reusable object pool is already closed.");

            // we shall not proceed any further...
            return true;
        }

        // disposing all the reusable objects...
        for (var i = 0; i < reusableObjects.length; ++i) {
            // retrieving available reusable object...
            final var reusableObject = retrieveReusableObject();
            // closing the reusable object...
            reusableObject.tryClose(true);
        }

        // clearing the reusable objects...
        reusableObjects = null;

        logger.log(Level.INFO, "Successfully closed reusable object pool.");

        return true;
    }

    @Override
    public void close() throws IOException {
        tryClose();
    }

    @Override
    public boolean tryClose() {
        logger.log(Level.INFO, "Closing reusable object pool.");

        // trying to set closed flag to true if the current flag value is false...
        final var alreadyClosed = setClosedIfNotClosed();

        // if the reusable object pool is already closed,
        // we shall not proceed any further...
        if (alreadyClosed) {
            logger.log(Level.INFO, "Reusable object pool is already closed.");

            return true;
        }

        // otherwise, we shall dispose the pool...
        return dispose();
    }
}
