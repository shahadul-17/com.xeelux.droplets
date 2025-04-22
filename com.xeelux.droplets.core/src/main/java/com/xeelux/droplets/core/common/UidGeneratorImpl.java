package com.xeelux.droplets.core.common;

import com.xeelux.droplets.core.concurrency.ThreadSafeExecutor;
import com.xeelux.droplets.core.configurations.ConfigurationProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class UidGeneratorImpl implements UidGenerator {

    private volatile long count = INITIAL_COUNT;
    private volatile long previousTimeInMilliseconds = -1L;

    private final Lock lock = new ReentrantLock(false);

    private static final Logger logger = LogManager.getLogger(UidGeneratorImpl.class);
    private static final int MINIMUM_RANDOM_VALUE = 1000;
    private static final int MAXIMUM_RANDOM_VALUE = 9999;
    private static final long INITIAL_COUNT = 1L;

    UidGeneratorImpl() { }

    private long getNextCount(final long currentTimeInMilliseconds) {
        return ThreadSafeExecutor.execute(lock, () -> {
            // computing next count...
            // if previous time is equal to the current time...
            final var count = previousTimeInMilliseconds == currentTimeInMilliseconds
                    ? this.count + 1            // <-- we shall increment the count...
                    : INITIAL_COUNT;            // <-- otherwise, we shall reset the count with the initial value...

            // then we shall re-assign the global count...
            this.count = count;
            // we'll then assign the current time to the previous time (global)...
            previousTimeInMilliseconds = currentTimeInMilliseconds;

            // lastly, we shall return the count...
            return count;
        });
    }

    @Override
    public String generate() {
        // retrieving the unique value of this application instance from configuration...
        // NOTE: THIS UNIQUE VALUE ENSURES THAT NO TWO APPLICATION INSTANCES CAN GENERATE THE SAME UNIQUE ID...
        final var uniqueValue = ConfigurationProvider.getConfiguration().getUniqueValue();
        // taking the enhanced current system time (in milliseconds)...
        final var currentTimeInMilliseconds = EnhancedTimeProvider.getCurrentTimeInMilliseconds();
        // getting the next count value...
        final var count = getNextCount(currentTimeInMilliseconds);
        // getting a thread local random generator...
        final Random random = ThreadLocalRandom.current();
        // we shall generate a random value within the pre-defined range...
        final var randomValue = random.nextInt(MINIMUM_RANDOM_VALUE, MAXIMUM_RANDOM_VALUE) + 1;

        // appending all the values to prepare a unique ID...
        // lastly, we shall return the unique ID...
        return uniqueValue + currentTimeInMilliseconds + count + randomValue;
    }
}
