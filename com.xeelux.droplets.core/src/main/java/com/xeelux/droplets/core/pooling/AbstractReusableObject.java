package com.xeelux.droplets.core.pooling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractReusableObject implements ReusableObject {

    private final Logger logger = LogManager.getLogger(AbstractReusableObject.class);
    private final AtomicBoolean available = new AtomicBoolean(true);

    @Override
    public boolean isAvailable() {
        return available.get();
    }

    @Override
    public void markAvailable() {
        available.getAndSet(true);
    }

    @Override
    public void markUnavailable() {
        available.getAndSet(false);
    }

    @Override
    public boolean markUnavailableIfAvailable() {
        // setting false if current value is true.
        // if new value is not set, this method shall return false. otherwise, it shall return true.
        // NOTE 1: IF CURRENT VALUE IS NOT TRUE, compareAndSet() METHOD WILL RETURN FALSE (WHICH IS THE SAME AS THE CURRENT VALUE).
        // OTHERWISE, IF THE NEW VALUE IS SET, compareAndSet() METHOD WILL RETURN TRUE (WHICH IS THE SAME AS THE CURRENT VALUE)...
        // NOTE 2: THIS METHOD ALWAYS RETURNS THE PREVIOUS VALUE (BEFORE SETTING/UPDATING)...
        return available.compareAndSet(true, false);
    }

    @Override
    public void release() {
        // marks the reusable object as available...
        markAvailable();

        final var reusableObjectPool = getReusableObjectPool();

        if (reusableObjectPool == null) { return; }

        reusableObjectPool.notifyReusableObjectAvailability(this);
    }

    @Override
    public void close() throws IOException {
        // we'll just call the tryClose() method...
        tryClose(true);
    }

    @Override
    public boolean tryClose(final boolean dispose) {
        // if the reusable object shall be disposed or
        // reusable object pool is null...
        if (dispose || getReusableObjectPool() == null) {
            // we shall call the dispose() method...
            dispose();

            // and we'll halt the execution of this method...
            return true;
        }

        // otherwise, we shall release this object and
        // return it back to the pool...
        release();

        // and return true...
        return true;
    }
}
