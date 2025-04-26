package com.xeelux.droplets.core.modules.filetransfer.client;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.modules.filetransfer.ConnectionType;
import com.xeelux.droplets.core.modules.filetransfer.FileTransferConnection;
import com.xeelux.droplets.core.modules.filetransfer.FileTransferConnectionImpl;
import com.xeelux.droplets.core.modules.filetransfer.FileTransferState;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventArguments;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventHandlerImpl;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventListener;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventType;
import com.xeelux.droplets.core.modules.filetransfer.server.*;
import com.xeelux.droplets.core.utilities.CloseableUtilities;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransferClientImpl implements FileTransferClient, Runnable {

    private final Logger logger = LogManager.getLogger(FileTransferClientImpl.class);

    private Thread listenerThread = null;

    private final InetAddress address;
    private final FileTransferClientConfiguration configuration;
    private final FileTransferState state = FileTransferState.getInstance("");
    private final EventHandler<FileTransferEventArguments, FileTransferEventListener> eventHandler
            = new FileTransferEventHandlerImpl();
    private FileTransferConnection connection = null;

    public FileTransferClientImpl(final FileTransferClientConfiguration configuration) {
        this.configuration = configuration;
        // preparing the address...
        address = new InetSocketAddress(this.configuration.getHost(), this.configuration.getPort()).getAddress();
    }

    @Override
    public FileTransferClientConfiguration getConfiguration() { return configuration; }

    @Override
    public FileTransferConnection getConnection() { return connection; }

    @Override
    public EventHandler<FileTransferEventArguments, FileTransferEventListener> getEventHandler() { return eventHandler; }

    @Override
    public void start() {
        // checking if the client is already running and setting the running flag to true...
        // NOTE: WE ARE USING STATE OBJECT BECAUSE
        // WE WANT TO MAKE SURE THAT ALL THE OTHER THREADS
        // GET STOPPED AT ONCE...
        if (state.getAndSetRunning(true)) {
            logger.log(Level.WARN, "File transfer client is already running.");

            return;
        }

        // creating new thread on which the client shall run...
        listenerThread = new Thread(this);
        listenerThread.setName("File Transfer Client");
        listenerThread.start();
    }

    @Override
    public void stop() {
        // sets the running flag to false...
        // NOTE: WE ARE USING STATE OBJECT BECAUSE
        // WE WANT TO MAKE SURE THAT ALL THE OTHER THREADS
        // GET STOPPED AT ONCE...
        state.setRunning(false);

        if (listenerThread == null) { return; }

        try {
            listenerThread.interrupt();
        } catch (final Throwable throwable) {
            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setMessage("An exception occurred while interrupting the listener thread.")
                    .setThrowable(throwable)
                    .executeEventListener(eventHandler);
        }
    }

    @Override
    public void join() {
        if (listenerThread == null) { return; }

        try {
            listenerThread.join();
        } catch (final Throwable throwable) {
            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setMessage("An exception occurred while waiting for the listener thread to finish.")
                    .setThrowable(throwable)
                    .executeEventListener(eventHandler);
        }
    }

    @Override
    public void run() {
        Socket clientSocket;

        try {
            // creating a server socket...
            clientSocket = new Socket(address, configuration.getPort());
        } catch (final Throwable throwable) {
            // executing event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setMessage("An exception occurred while binding the file transfer server on " + configuration.getHost() + ":" + configuration.getPort())
                    .setThrowable(throwable)
                    .executeEventListener(eventHandler);

            // we must stop the server (which sets running to false)...
            stop();

            // we shall not proceed any further...
            return;
        }

        // executing event listener...
        FileTransferEventArguments.createInstance()
                .setSender(this)
                .setEventType(FileTransferEventType.CLIENT_START)
                .executeEventListener(eventHandler);

        connection = new FileTransferConnectionImpl(1L, clientSocket, eventHandler);

        final var initialized = connection.initialize();

        if (!initialized) { return; }

        // executing event listener...
        FileTransferEventArguments.createInstance()
                .setSender(this)
                .setEventType(FileTransferEventType.CLIENT_CONNECT)
                .setConnectionId(connection.getConnectionId())
                .setRemoteHost(connection.getRemoteHost())
                .setRemotePort(connection.getRemotePort())
                .executeEventListener(connection.getEventHandler());

        // if writing connection type fails...
        if (!connection.tryWriteByte(ConnectionType.CONTROL.getValue())) { return; }
        // if flushing fails...
        if (!connection.tryFlush()) { return; }

        // we shall receive the connection ID...
        final var connectionId = connection.tryReadInt64();

        System.out.println("Connection ID = " + connectionId);

        // closing the server socket...
        // CloseableUtilities.tryClose(serverSocket);
        // executing event listener...
        /*FileTransferEventArguments.createInstance()
                .setSender(this)
                .setEventType(FileTransferEventType.CLIENT_STOP)
                .executeEventListener(eventHandler);*/
    }
}
