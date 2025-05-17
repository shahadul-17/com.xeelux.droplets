package com.xeelux.droplets.core.networking.http.server;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.modules.filetransfer.FileTransferConnection;
import com.xeelux.droplets.core.networking.http.server.event.HttpServerEventArguments;
import com.xeelux.droplets.core.networking.http.server.event.HttpServerEventListener;

import java.net.Socket;

interface HttpRequestHandler extends Runnable {
    long getRequestId();
    FileTransferConnection getConnection();
    HttpServer getParent();
    void start();

    default EventHandler<HttpServerEventArguments, HttpServerEventListener> getEventHandler() {
        return getParent().getEventHandler();
    }

    static HttpRequestHandler createInstance(
            final long requestId,
            final Socket clientSocket,
            final HttpServer parent) {
        return new HttpRequestHandlerImpl(requestId, clientSocket, parent);
    }
}
