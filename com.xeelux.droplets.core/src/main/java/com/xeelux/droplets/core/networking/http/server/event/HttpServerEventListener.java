package com.xeelux.droplets.core.networking.http.server.event;

public interface HttpServerEventListener {
    default void onServerStart(final HttpServerEventArguments eventArguments) { }
    default void onServerAwaitingConnection(final HttpServerEventArguments eventArguments) { }
    default void onServerStop(final HttpServerEventArguments eventArguments) { }
    default void onClientConnect(final HttpServerEventArguments eventArguments) { }
    default void onClientDisconnect(final HttpServerEventArguments eventArguments) { }
    default void onBeforeContentSend(final HttpServerEventArguments eventArguments) { }
    default void onContentSend(final HttpServerEventArguments eventArguments) { }
    default void onContentReceive(final HttpServerEventArguments eventArguments) { }
    default void onException(final HttpServerEventArguments eventArguments) { }
}
