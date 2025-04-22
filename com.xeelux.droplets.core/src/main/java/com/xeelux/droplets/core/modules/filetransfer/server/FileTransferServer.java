package com.xeelux.droplets.core.modules.filetransfer.server;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventArguments;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventListener;

public interface FileTransferServer {

    FileTransferServerConfiguration getConfiguration();
    EventHandler<FileTransferEventArguments, FileTransferEventListener> getEventHandler();
    void start();
    void stop();
    void join();
}
