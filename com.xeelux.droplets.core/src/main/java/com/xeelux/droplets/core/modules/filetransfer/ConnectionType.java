package com.xeelux.droplets.core.modules.filetransfer;

import java.util.HashMap;
import java.util.Map;

public enum ConnectionType {

    NONE((byte) 0),
    CONTROL((byte) 1),
    TEXT((byte) 2),
    STREAM((byte) 3);

    private final byte value;

    private static final Map<Byte, ConnectionType> valueMap;

    static {
        // populates a map from values because every time
        // the static method values() is called, it initializes a new array...
        final var connectionTypes = ConnectionType.values();
        // we don't want to waste memory so, we shall initialize the maps
        // with an initial capacity set to the length of the connection types array...
        valueMap = new HashMap<>(connectionTypes.length * 2);

        // puts all the values in the map...
        for (final var connectionType : connectionTypes) {
            // we shall put the connection type in the value map if the name and value
            // does not already exist in the maps...
            valueMap.putIfAbsent(connectionType.value, connectionType);
        }
    }

    ConnectionType(final byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Byte.toString(getValue());
    }

    public static ConnectionType from(final Byte value) {
        if (value == null || value < NONE.getValue()) { return NONE; }

        // then we shall look for the connection type in the value map...
        final var connectionType = valueMap.get(value);

        // if connection type is not found for the value, we shall return NONE...
        if (connectionType == null) { return NONE; }

        // otherwise, we'll return the connection type...
        return connectionType;
    }
}
