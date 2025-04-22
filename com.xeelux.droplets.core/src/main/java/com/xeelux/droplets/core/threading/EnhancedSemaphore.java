package com.xeelux.droplets.core.threading;

import java.util.concurrent.Semaphore;

public class EnhancedSemaphore extends Semaphore {

    private final int permits;

    public EnhancedSemaphore(final int permits) {
        super(permits);

        this.permits = permits;
    }

    public EnhancedSemaphore(final int permits, final boolean fair) {
        super(permits, fair);

        this.permits = permits;
    }

    public int getPermits() {
        return permits;
    }
}
