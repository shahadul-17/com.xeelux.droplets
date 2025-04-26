package com.xeelux.droplets.core.modules.filetransfer.client;

import com.xeelux.droplets.core.text.JsonSerializable;

public interface FileTransferClientConfiguration extends JsonSerializable {
    String getHost();
    FileTransferClientConfiguration setHost(final String host);

    int getPort();
    FileTransferClientConfiguration setPort(final int port);

    static FileTransferClientConfiguration create() {
        return new FileTransferClientConfigurationImpl();
    }
}
