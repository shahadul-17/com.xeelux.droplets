package com.xeelux.droplets.core.networking.http.server.event;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.networking.http.server.HttpServer;
import com.xeelux.droplets.core.networking.http.server.HttpServerException;
import com.xeelux.droplets.core.text.JsonSerializable;

public interface HttpServerEventArguments extends JsonSerializable {

    HttpServerEventType getEventType();
    HttpServerEventArguments setEventType(final HttpServerEventType eventType);

    long getConnectionId();
    HttpServerEventArguments setConnectionId(final long connectionId);

    String getRemoteHost();
    HttpServerEventArguments setRemoteHost(final String remoteHost);

    int getRemotePort();
    HttpServerEventArguments setRemotePort(final int remotePort);

    HttpServer getSender();
    HttpServerEventArguments setSender(final HttpServer sender);

    String getMessage();
    HttpServerEventArguments setMessage(final String message);

    HttpServerException getException();
    HttpServerEventArguments setException(final HttpServerException exception);

    default HttpServerEventArguments executeEventListener(
            final EventHandler<HttpServerEventArguments, HttpServerEventListener> eventHandler) {
        eventHandler.executeEventListener(this);

        return this;
    }

    static HttpServerEventArguments createInstance() {
        return new HttpServerEventArgumentsImpl();
    }
}
