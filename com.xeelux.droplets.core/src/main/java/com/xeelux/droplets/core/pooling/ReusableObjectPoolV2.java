package com.xeelux.droplets.core.pooling;

import com.xeelux.droplets.core.concurrency.ThreadSafeBlockingQueue;
import com.xeelux.droplets.core.concurrency.ThreadSafeBlockingQueueImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

class ReusableObjectPoolV2 extends ReusableObjectPoolV1 {

    private final Logger logger = LogManager.getLogger(ReusableObjectPoolV2.class);
    private ThreadSafeBlockingQueue<ReusableObject> reusableObjectQueue;

    private static final String TEXT = "Reusable Object Pool (Version 2)";

    ReusableObjectPoolV2(final ReusableObjectInstantiator instantiator) {
        super(null, instantiator);
    }

    ReusableObjectPoolV2(
            final Map<String, Object> options,
            final ReusableObjectInstantiator instantiator) {
        super(options, instantiator);
    }

    @Override
    protected void initialize() {
        // retrieving the capacity...
        final var capacity = getCapacity();
        // initializing the reusable object queue...
        reusableObjectQueue = new ThreadSafeBlockingQueueImpl<>(capacity);

        // retrieving the options...
        final var options = getOptions();
        // retrieving the instantiator...
        // NOTE: INSTANTIATOR CAN NOT BE NULL...!!!
        final var instantiator = getInstantiator();

        // initializing the reusable objects...
        for (var i = 0; i < capacity; ++i) {
            // instantiating reusable object...
            final var reusableObject = instantiator.instantiate(i, options, this);
            // and placing them into the queue...
            reusableObjectQueue.enqueue(reusableObject);
        }
    }

    @Override
    protected ReusableObject retrieveReusableObject() {
        return reusableObjectQueue.dequeue();
    }

    @Override
    public void notifyReusableObjectAvailability(final ReusableObject reusableObject) {
        reusableObjectQueue.enqueue(reusableObject);
    }

    @Override
    public String toString() {
        return TEXT;
    }

    @Override
    protected boolean dispose() {
        // if the queue is null...
        if (reusableObjectQueue == null) {
            logger.log(Level.INFO, "Reusable object pool is already closed.");

            // we shall not proceed any further...
            return true;
        }

        // retrieving the capacity...
        final var capacity = getCapacity();

        // disposing all the reusable objects...
        for (var i = 0; i < capacity; ++i) {
            // retrieving available reusable object...
            final var reusableObject = retrieveReusableObject();
            // disposing the reusable object...
            reusableObject.tryClose(true);
        }

        // clearing the reusable objects...
        reusableObjectQueue.clear();
        // and assigning null to the collection of reusable objects...
        reusableObjectQueue = null;

        logger.log(Level.INFO, "Successfully closed reusable object pool.");

        return true;
    }
}
