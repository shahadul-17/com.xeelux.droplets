package com.xeelux.droplets.core.modules.filetransfer.server;

public class FileTransferServerConfigurationImpl implements FileTransferServerConfiguration {

    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    private int backlog = DEFAULT_BACKLOG;

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 51482;
    private static final int DEFAULT_BACKLOG = 256;

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public FileTransferServerConfiguration setHost(final String host) {
        this.host = host;

        return this;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public FileTransferServerConfiguration setPort(final int port) {
        this.port = port < 1 ? DEFAULT_PORT : port;

        return this;
    }

    @Override
    public int getBacklog() {
        return backlog;
    }

    @Override
    public FileTransferServerConfiguration setBacklog(final int backlog) {
        this.backlog = backlog < 1 ? DEFAULT_BACKLOG : backlog;

        return this;
    }

    @Override
    public String toString() {
        return toJson(true);
    }
}
