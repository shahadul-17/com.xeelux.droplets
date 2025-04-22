package com.xeelux.droplets.core.modules.filetransfer;

import com.xeelux.droplets.core.dependencyinjection.ServiceProvider;
import com.xeelux.droplets.core.text.JsonSerializable;

public interface FileTransferState extends JsonSerializable {

    boolean isRunning();
    boolean getAndSetRunning(final boolean running);
    FileTransferState setRunning(final boolean running);

    boolean isConnected();
    boolean getAndSetConnected(final boolean connected);
    FileTransferState setConnected(final boolean connected);

    static FileTransferState getInstance(final String context) {
        final var key = context + "_" + FileTransferStateImpl.class.getName();

        return ServiceProvider.getSingleton().get(key, FileTransferStateImpl.class);
    }
}
