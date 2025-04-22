package com.xeelux.droplets.core.configurations;

import com.xeelux.droplets.core.text.JsonSerializable;

public class VirtualThreadSchedulerConfiguration implements JsonSerializable {

    private int availablePlatformThreadCount = 16;                  // <-- we are assigning a default value...
    private int maximumPlatformThreadPoolSize = 192;                // <-- we are assigning a default value...
    private int minimumUnblockedPlatformThreadCount = 4;            // <-- we are assigning a default value...

    public int getAvailablePlatformThreadCount() {
        return availablePlatformThreadCount;
    }

    public VirtualThreadSchedulerConfiguration setAvailablePlatformThreadCount(final int availablePlatformThreadCount) {
        this.availablePlatformThreadCount = availablePlatformThreadCount;

        return this;
    }

    public int getMaximumPlatformThreadPoolSize() {
        return maximumPlatformThreadPoolSize;
    }

    public VirtualThreadSchedulerConfiguration setMaximumPlatformThreadPoolSize(final int maximumPlatformThreadPoolSize) {
        this.maximumPlatformThreadPoolSize = maximumPlatformThreadPoolSize;

        return this;
    }

    public int getMinimumUnblockedPlatformThreadCount() {
        return minimumUnblockedPlatformThreadCount;
    }

    public VirtualThreadSchedulerConfiguration setMinimumUnblockedPlatformThreadCount(final int minimumUnblockedPlatformThreadCount) {
        this.minimumUnblockedPlatformThreadCount = minimumUnblockedPlatformThreadCount;

        return this;
    }

    @Override
    public String toString() {
        return toJson(true);
    }
}
