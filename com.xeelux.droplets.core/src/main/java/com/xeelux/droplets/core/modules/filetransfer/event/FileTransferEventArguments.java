package com.xeelux.droplets.core.modules.filetransfer.event;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.text.JsonSerializable;

public interface FileTransferEventArguments extends JsonSerializable {

    FileTransferEventType getEventType();
    FileTransferEventArguments setEventType(final FileTransferEventType eventType);

    long getConnectionId();
    FileTransferEventArguments setConnectionId(final long connectionId);

    String getRemoteHost();
    FileTransferEventArguments setRemoteHost(final String remoteHost);

    int getRemotePort();
    FileTransferEventArguments setRemotePort(final int remotePort);

    Object getSender();
    FileTransferEventArguments setSender(final Object sender);

    String getMessage();
    FileTransferEventArguments setMessage(final String message);

    Throwable getThrowable();
    FileTransferEventArguments setThrowable(final Throwable throwable);

    default FileTransferEventArguments executeEventListener(
            final EventHandler<FileTransferEventArguments, FileTransferEventListener> eventHandler) {
        eventHandler.executeEventListener(this);

        return this;
    }

    static FileTransferEventArguments createInstance() {
        return new FileTransferEventArgumentsImpl();
    }
}
