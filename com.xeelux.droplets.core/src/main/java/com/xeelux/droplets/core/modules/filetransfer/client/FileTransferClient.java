package com.xeelux.droplets.core.modules.filetransfer.client;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.modules.filetransfer.FileTransferConnection;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventArguments;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventListener;

public interface FileTransferClient {

    FileTransferClientConfiguration getConfiguration();
    FileTransferConnection getConnection();
    EventHandler<FileTransferEventArguments, FileTransferEventListener> getEventHandler();
    void start();
    void stop();
    void join();
}
