package com.xeelux.droplets.core.modules.filetransfer.server;

import com.xeelux.droplets.core.text.JsonSerializable;

public interface FileTransferServerConfiguration extends JsonSerializable {
    String getHost();
    FileTransferServerConfiguration setHost(final String host);

    int getPort();
    FileTransferServerConfiguration setPort(final int port);

    int getBacklog();
    FileTransferServerConfiguration setBacklog(final int backlog);

    static FileTransferServerConfiguration createInstance() {
        return new FileTransferServerConfigurationImpl();
    }
}
