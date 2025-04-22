package com.xeelux.droplets.core.modules.filetransfer.server;

public interface FileTransferClientHandlerCollection {
    FileTransferClientHandlerCollection put(final Long key, FileTransferClientHandler clientHandler);
    FileTransferClientHandler get(final Long key);
    FileTransferClientHandler remove(final Long key);
    FileTransferClientHandlerCollection clear();
}
