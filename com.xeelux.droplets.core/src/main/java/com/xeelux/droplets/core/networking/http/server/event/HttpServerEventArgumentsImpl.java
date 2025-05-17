package com.xeelux.droplets.core.networking.http.server.event;

import com.xeelux.droplets.core.networking.http.server.HttpServer;
import com.xeelux.droplets.core.networking.http.server.HttpServerException;
import com.xeelux.droplets.core.utilities.StringUtilities;

class HttpServerEventArgumentsImpl implements HttpServerEventArguments {

    private HttpServerEventType eventType = HttpServerEventType.NONE;    // <-- type of the event...
    private long connectionId = 0L;                                          // <-- this value can be used to uniquely identify the connection...
    private String remoteHost = StringUtilities.getEmptyString();
    private int remotePort = 0;
    private HttpServer sender = null;                                            // <-- the object that sent the event...
    private String message = StringUtilities.getEmptyString();               // <-- any message that needs to be passed to the event listener...
    private HttpServerException exception = null;                                      // <-- holds any throwable/exception...

    HttpServerEventArgumentsImpl() { }

    @Override
    public HttpServerEventType getEventType() {
        return eventType;
    }

    @Override
    public HttpServerEventArgumentsImpl setEventType(final HttpServerEventType eventType) {
        this.eventType = eventType;

        return this;
    }

    @Override
    public long getConnectionId() {
        return connectionId;
    }

    @Override
    public HttpServerEventArgumentsImpl setConnectionId(final long connectionId) {
        this.connectionId = connectionId;

        return this;
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    @Override
    public HttpServerEventArgumentsImpl setRemoteHost(final String remoteHost) {
        this.remoteHost = remoteHost;

        return this;
    }

    @Override
    public int getRemotePort() {
        return remotePort;
    }

    @Override
    public HttpServerEventArgumentsImpl setRemotePort(final int remotePort) {
        this.remotePort = remotePort;

        return this;
    }

    @Override
    public HttpServer getSender() {
        return sender;
    }

    @Override
    public HttpServerEventArgumentsImpl setSender(final HttpServer sender) {
        this.sender = sender;

        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpServerEventArgumentsImpl setMessage(final String message) {
        this.message = message;

        return this;
    }

    @Override
    public HttpServerException getException() {
        return exception;
    }

    @Override
    public HttpServerEventArgumentsImpl setException(final HttpServerException exception) {
        this.exception = exception;

        return this;
    }

    @Override
    public String toString() {
        return toJson(true);
    }
}
