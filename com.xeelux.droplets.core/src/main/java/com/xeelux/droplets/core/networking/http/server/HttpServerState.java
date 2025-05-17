package com.xeelux.droplets.core.networking.http.server;

import com.xeelux.droplets.core.text.JsonSerializable;

interface HttpServerState extends JsonSerializable {

    boolean isRunning();
    boolean getAndSetRunning(final boolean running);
    HttpServerState setRunning(final boolean running);

    boolean isConnected();
    boolean getAndSetConnected(final boolean connected);
    HttpServerState setConnected(final boolean connected);
}
