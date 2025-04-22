package com.xeelux.droplets.core.modules.filetransfer.server;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.modules.filetransfer.ConnectionType;
import com.xeelux.droplets.core.modules.filetransfer.FileTransferConnection;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventArguments;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventListener;

import java.net.Socket;

public interface FileTransferClientHandler extends Runnable {
    ConnectionType getConnectionType();
    FileTransferConnection getConnection();
    EventHandler<FileTransferEventArguments, FileTransferEventListener> getEventHandler();
    void start();

    static FileTransferClientHandler createInstance(
            final long uniqueClientIdentifier,
            final Socket clientSocket,
            final FileTransferClientHandlerCollection clientHandlerCollection,
            final EventHandler<FileTransferEventArguments, FileTransferEventListener> eventHandler) {
        return new FileTransferClientHandlerImpl(uniqueClientIdentifier, clientSocket, clientHandlerCollection, eventHandler);
    }
}
