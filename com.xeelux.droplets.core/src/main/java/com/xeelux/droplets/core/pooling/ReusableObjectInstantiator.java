package com.xeelux.droplets.core.pooling;

import java.util.Map;

public interface ReusableObjectInstantiator {
    ReusableObject instantiate(
            final int index,
            final Map<String, Object> options,
            final ReusableObjectPool reusableObjectPool);
}
