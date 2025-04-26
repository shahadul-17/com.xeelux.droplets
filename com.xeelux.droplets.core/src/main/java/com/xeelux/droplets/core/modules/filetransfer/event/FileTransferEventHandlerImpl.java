package com.xeelux.droplets.core.modules.filetransfer.event;

import com.xeelux.droplets.core.common.AbstractEventHandler;
import com.xeelux.droplets.core.threading.AsyncTask;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileTransferEventHandlerImpl
        extends AbstractEventHandler<FileTransferEventArguments, FileTransferEventListener>
        implements FileTransferEventHandler {

    private final Logger logger = LogManager.getLogger(FileTransferEventHandlerImpl.class);

    @Override
    public void executeEventListener(final FileTransferEventArguments eventArguments) {
        // in-case of null or NONE event type, we shall not proceed any further...
        if (eventArguments.getEventType() == null
                || eventArguments.getEventType() == FileTransferEventType.NONE) { return; }

        // getting the list of event listeners...
        final var eventListeners = getEventListeners();

        // iterating over all the event listeners...
        for (final var eventListener : eventListeners) {
            // we must run these tasks asynchronously...
            AsyncTask.run(() -> {
                try {
                    switch (eventArguments.getEventType()) {
                        case CLIENT_START -> eventListener.onClientStart(eventArguments);
                        case CLIENT_STOP -> eventListener.onClientStop(eventArguments);
                        case SERVER_START -> eventListener.onServerStart(eventArguments);
                        case SERVER_AWAITING_CONNECTION -> eventListener.onServerAwaitingConnection(eventArguments);
                        case SERVER_STOP -> eventListener.onServerStop(eventArguments);
                        case CLIENT_CONNECT -> eventListener.onClientConnect(eventArguments);
                        case CLIENT_DISCONNECT -> eventListener.onClientDisconnect(eventArguments);
                        case BEFORE_CONTENT_SEND -> eventListener.onBeforeContentSend(eventArguments);
                        case CONTENT_SEND -> eventListener.onContentSend(eventArguments);
                        case CONTENT_RECEIVE -> eventListener.onContentReceive(eventArguments);
                        case EXCEPTION -> eventListener.onException(eventArguments);
                        default -> logger.log(Level.WARN, "Ignored execution of unknown event type, '{}'.", eventArguments.getEventType().name());
                    }
                } catch (final Throwable throwable) {
                    logger.log(Level.ERROR, "An exception occurred while calling the event listener.", throwable);
                }
            });
        }
    }
}
