package com.xeelux.droplets.core.modules.filetransfer;

public class RequestHeaderImpl implements RequestHeader {

    private ConnectionType connectionType = ConnectionType.NONE;

    @Override
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    @Override
    public RequestHeader setConnectionType(final ConnectionType connectionType) {
        this.connectionType = connectionType;

        return null;
    }

    @Override
    public String toString() {
        return toJson(true);
    }
}
