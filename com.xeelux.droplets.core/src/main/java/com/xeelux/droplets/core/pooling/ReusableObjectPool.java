package com.xeelux.droplets.core.pooling;

import java.io.Closeable;
import java.util.Map;

public interface ReusableObjectPool extends Closeable {
    boolean isClosed();
    int getCapacity();
    Map<String, Object> getOptions();
    void setOptions(final Map<String, Object> options);
    ReusableObject getReusableObject();
    boolean tryClose();

    default void notifyReusableObjectAvailability(final ReusableObject reusableObject) { }

    static ReusableObjectPool createInstance(final ReusableObjectInstantiator instantiator) {
        return createInstance(null, instantiator);
    }

    static ReusableObjectPool createInstance(
            final Map<String, Object> options,
            final ReusableObjectInstantiator instantiator) {
        var version = 2;        // <-- version two (2) is our default version...

        // if option is provided...
        if (options != null) {
            // we shall retrieve the version...
            version = (int) options.getOrDefault("version", 2);
        }

        return switch (version) {
            case 1 -> new ReusableObjectPoolV1(options, instantiator);
            default -> new ReusableObjectPoolV2(options, instantiator);
        };
    }
}
