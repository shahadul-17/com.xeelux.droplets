package com.xeelux.droplets.core.common;

import com.xeelux.droplets.core.dependencyinjection.ServiceProvider;

public interface UidGenerator {

    String generate();

    static UidGenerator getInstance() {
        final var serviceProvider = ServiceProvider.getSingleton();
        final var uidGenerator = serviceProvider.get(UidGenerator.class, UidGeneratorImpl::new);

        return uidGenerator;
    }
}
