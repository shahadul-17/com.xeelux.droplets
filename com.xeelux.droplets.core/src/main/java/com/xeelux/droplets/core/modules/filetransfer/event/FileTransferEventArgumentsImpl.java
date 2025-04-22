package com.xeelux.droplets.core.modules.filetransfer.event;

import com.xeelux.droplets.core.utilities.StringUtilities;

class FileTransferEventArgumentsImpl implements FileTransferEventArguments {

    private FileTransferEventType eventType = FileTransferEventType.NONE;    // <-- type of the event...
    private long connectionId = 0L;                                          // <-- this value can be used to uniquely identify the connection...
    private String remoteHost = StringUtilities.getEmptyString();
    private int remotePort = 0;
    private Object sender = null;                                            // <-- the object that sent the event...
    private String message = StringUtilities.getEmptyString();               // <-- any message that needs to be passed to the event listener...
    private Throwable throwable = null;                                      // <-- holds any throwable/exception...

    FileTransferEventArgumentsImpl() { }

    @Override
    public FileTransferEventType getEventType() {
        return eventType;
    }

    @Override
    public FileTransferEventArgumentsImpl setEventType(final FileTransferEventType eventType) {
        this.eventType = eventType;

        return this;
    }

    @Override
    public long getConnectionId() {
        return connectionId;
    }

    @Override
    public FileTransferEventArgumentsImpl setConnectionId(final long connectionId) {
        this.connectionId = connectionId;

        return this;
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    @Override
    public FileTransferEventArgumentsImpl setRemoteHost(final String remoteHost) {
        this.remoteHost = remoteHost;

        return this;
    }

    @Override
    public int getRemotePort() {
        return remotePort;
    }

    @Override
    public FileTransferEventArgumentsImpl setRemotePort(final int remotePort) {
        this.remotePort = remotePort;

        return this;
    }

    @Override
    public Object getSender() {
        return sender;
    }

    @Override
    public FileTransferEventArgumentsImpl setSender(final Object sender) {
        this.sender = sender;

        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public FileTransferEventArgumentsImpl setMessage(final String message) {
        this.message = message;

        return this;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public FileTransferEventArgumentsImpl setThrowable(final Throwable throwable) {
        this.throwable = throwable;

        return this;
    }

    @Override
    public String toString() {
        return toJson(true);
    }
}
