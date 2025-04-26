package com.xeelux.droplets.core.modules.filetransfer.server;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.modules.filetransfer.ConnectionType;
import com.xeelux.droplets.core.modules.filetransfer.FileTransferConnection;
import com.xeelux.droplets.core.modules.filetransfer.FileTransferConnectionImpl;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventArguments;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventListener;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventType;
import com.xeelux.droplets.core.threading.AsyncTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

class FileTransferClientHandlerImpl implements FileTransferClientHandler {

    private final Logger logger = LogManager.getLogger(FileTransferClientHandlerImpl.class);
    private ConnectionType connectionType = ConnectionType.NONE;
    private final FileTransferConnection connection;
    private final FileTransferClientHandlerCollection clientHandlerCollection;

    FileTransferClientHandlerImpl(
            final long connectionId,
            final Socket clientSocket,
            final FileTransferClientHandlerCollection clientHandlerCollection,
            final EventHandler<FileTransferEventArguments, FileTransferEventListener> eventHandler) {
        this.clientHandlerCollection = clientHandlerCollection;
        connection = new FileTransferConnectionImpl(connectionId, clientSocket, eventHandler);
    }

    @Override
    public ConnectionType getConnectionType() {
        return connectionType;
    }

    @Override
    public FileTransferConnection getConnection() {
        return connection;
    }

    @Override
    public EventHandler<FileTransferEventArguments, FileTransferEventListener> getEventHandler() {
        return connection.getEventHandler();
    }

    @Override
    public void start() {
        // we shall start handling clients asynchronously...
        AsyncTask.run(this);
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

        final var connectionTypeAsByte = connection.tryReadByte();
        connectionType = ConnectionType.from(connectionTypeAsByte);

        System.out.println(connectionType.name());

        switch (connectionType) {
            case CONTROL -> {
                if (!connection.tryWriteInt64(connection.getConnectionId())) { return; }
                if (!connection.tryFlush()) { return; }

                clientHandlerCollection.put(connection.getConnectionId(), this);
            }
            default -> {
                final var message = "Invalid connection type received from the client socket ("
                        + connection.getConnectionId() + "@" + connection.getRemoteHost() + ":" + connection.getRemotePort() + ").";

                // if exception occurs, executing the event listener...
                FileTransferEventArguments.createInstance()
                        .setSender(this)
                        .setEventType(FileTransferEventType.EXCEPTION)
                        .setConnectionId(connection.getConnectionId())
                        .setRemoteHost(connection.getRemoteHost())
                        .setRemotePort(connection.getRemotePort())
                        .setMessage(message)
                        .setThrowable(new Exception(message))
                        .executeEventListener(connection.getEventHandler());
            }
        }
    }
}
