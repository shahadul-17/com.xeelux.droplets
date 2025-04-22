package com.xeelux.droplets.core.modules.filetransfer.server;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.modules.filetransfer.FileTransferState;
import com.xeelux.droplets.core.modules.filetransfer.event.*;
import com.xeelux.droplets.core.utilities.CloseableUtilities;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransferServerImpl implements FileTransferServer, Runnable {

    private final Logger logger = LogManager.getLogger(FileTransferServerImpl.class);

    private Thread listenerThread = null;

    private final InetAddress address;
    private final FileTransferServerConfiguration configuration;
    private final FileTransferState state = FileTransferState.getInstance("");
    private final FileTransferClientHandlerCollection clientHandlerCollection = new FileTransferClientHandlerCollectionImpl();
    private final EventHandler<FileTransferEventArguments, FileTransferEventListener> eventHandler
            = new FileTransferEventHandlerImpl();

    public FileTransferServerImpl(final FileTransferServerConfiguration configuration) {
        this.configuration = configuration;
        // preparing the address...
        address = new InetSocketAddress(this.configuration.getHost(), this.configuration.getPort()).getAddress();
    }

    @Override
    public FileTransferServerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public EventHandler<FileTransferEventArguments, FileTransferEventListener> getEventHandler() {
        return eventHandler;
    }

    @Override
    public void start() {
        // checking if the server is already running and setting the running flag to true...
        // NOTE: WE ARE USING STATE OBJECT BECAUSE
        // WE WANT TO MAKE SURE THAT ALL THE OTHER THREADS
        // GET STOPPED AT ONCE...
        if (state.getAndSetRunning(true)) {
            logger.log(Level.WARN, "File transfer server is already running.");

            return;
        }

        // creating new thread on which the server shall run...
        listenerThread = new Thread(this);
        listenerThread.setName("File Transfer Server");
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
        ServerSocket serverSocket;

        try {
            // creating a server socket...
            serverSocket = new ServerSocket(configuration.getPort(), configuration.getBacklog(), address);
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
                .setEventType(FileTransferEventType.SERVER_START)
                .executeEventListener(eventHandler);

        var count = 1L;

        while (state.isRunning()) {
            Socket clientSocket;

            // executing event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.SERVER_AWAITING_CONNECTION)
                    .executeEventListener(eventHandler);

            try {
                // waiting for new client...
                clientSocket = serverSocket.accept();
            } catch (final Throwable throwable) {
                // if exception occurs, executing the event listener...
                FileTransferEventArguments.createInstance()
                        .setSender(this)
                        .setEventType(FileTransferEventType.EXCEPTION)
                        .setMessage("An exception occurred while accepting new file transfer client.")
                        .setThrowable(throwable)
                        .executeEventListener(eventHandler);

                // we shall skip this iteration...
                continue;
            }

            // getting the current count...
            final var uniqueClientIdentifier = count;
            // incrementing the count...
            ++count;

            // creating and starting a new client handler...
            FileTransferClientHandler
                    .createInstance(uniqueClientIdentifier, clientSocket, clientHandlerCollection, eventHandler)
                    .start();
        }

        // closing the server socket...
        CloseableUtilities.tryClose(serverSocket);
        // executing event listener...
        FileTransferEventArguments.createInstance()
                .setSender(this)
                .setEventType(FileTransferEventType.SERVER_STOP)
                .executeEventListener(eventHandler);
    }
}
