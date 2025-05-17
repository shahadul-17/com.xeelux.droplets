package com.xeelux.droplets.core.networking.http.server;

import com.xeelux.droplets.core.modules.filetransfer.FileTransferConnection;
import com.xeelux.droplets.core.modules.filetransfer.FileTransferConnectionImpl;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventArguments;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
import java.util.concurrent.ExecutorService;

class HttpRequestHandlerImpl implements HttpRequestHandler {

    private final Logger logger = LogManager.getLogger(HttpRequestHandlerImpl.class);

    private final long requestId;
    private final FileTransferConnection connection;
    private final HttpServer parent;
    private final ExecutorService executorService;

    HttpRequestHandlerImpl(
            final long requestId,
            final Socket clientSocket,
            final HttpServer parent) {
        this.requestId = requestId;
        this.parent = parent;
        executorService = parent.getConfiguration().getExecutorServiceProvider().getExecutorService();
        connection = new FileTransferConnectionImpl(requestId, clientSocket, getEventHandler());
    }

    @Override
    public long getRequestId() { return requestId; }

    @Override
    public HttpServer getParent() { return parent; }

    @Override
    public FileTransferConnection getConnection() {
        return connection;
    }

    @Override
    public void start() {
        // we shall start handling clients...
        executorService.execute(this);
    }

    @Override
    public void run() {
        // initializing the connection...
        // NOTE: THIS METHOD AUTOMATICALLY CLOSES UNDERLYING STREAMS AND/OR SOCKETS...
        final var initialized = connection.initialize();

        // if initialization fails, we shall not proceed any further...
        if (!initialized) { return; }

        // executing event listener...
        FileTransferEventArguments.createInstance()
                .setSender(this)
                .setEventType(FileTransferEventType.CLIENT_CONNECT)
                .setConnectionId(connection.getConnectionId())
                .setRemoteHost(connection.getRemoteHost())
                .setRemotePort(connection.getRemotePort())
                .executeEventListener(connection.getEventHandler());
    }
}
