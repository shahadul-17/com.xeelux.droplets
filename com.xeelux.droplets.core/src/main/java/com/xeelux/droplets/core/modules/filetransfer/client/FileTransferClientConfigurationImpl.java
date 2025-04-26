package com.xeelux.droplets.core.modules.filetransfer.client;

public class FileTransferClientConfigurationImpl implements FileTransferClientConfiguration {

    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 51482;

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public FileTransferClientConfiguration setHost(final String host) {
        this.host = host;

        return this;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public FileTransferClientConfiguration setPort(final int port) {
        this.port = port < 1 ? DEFAULT_PORT : port;

        return this;
    }

    @Override
    public String toString() {
        return toJson(true);
    }
}
