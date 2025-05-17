package com.xeelux.droplets.core.pooling;

import java.io.Closeable;

public interface ReusableObject extends Closeable {
    boolean isAvailable();
    void markAvailable();
    void markUnavailable();

    /**
     * This method shall mark this reusable object as unavailable
     * if and only if this object is currently available.
     * @implSpec This method must be thread safe.
     * @return True if this object was available before being marked as unavailable.
     * Otherwise, returns false.
     */
    boolean markUnavailableIfAvailable();
    int getReusableObjectId();

    /**
     * If the reusable object belongs to a pool, this method shall release
     * the reusable object and returns it back to the pool. Otherwise,
     * this method shall dispose this reusable object.
     * @param dispose If set to true, this method will dispose this reusable
     *                object even if this object belongs to a pool.
     * @return True if successful. Otherwise, returns false.
     */
    boolean tryClose(final boolean dispose);

    default ReusableObjectPool getReusableObjectPool() { return null; }

    default void dispose() { }

    default void release() { }

    /**
     * If the reusable object belongs to a pool, this method shall release
     * the reusable object and returns it back to the pool. Otherwise,
     * this method shall dispose this reusable object.
     * @return True if successful. Otherwise, returns false.
     */
    default boolean tryClose() {
        return tryClose(false);
    }
}
