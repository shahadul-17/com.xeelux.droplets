package com.xeelux.droplets.core.dependencyinjection;

public interface ServiceInstantiator<Type> {
    Type instantiate();
}
