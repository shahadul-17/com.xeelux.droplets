package com.xeelux.droplets.core.modules.filetransfer;

import com.xeelux.droplets.core.concurrency.ThreadSafeBoolean;

class FileTransferStateImpl implements FileTransferState {

    private final ThreadSafeBoolean running = new ThreadSafeBoolean();
    private final ThreadSafeBoolean connected = new ThreadSafeBoolean();

    private FileTransferStateImpl() { }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public boolean getAndSetRunning(final boolean running) {
        return this.running.getAndSet(running);
    }

    @Override
    public FileTransferState setRunning(final boolean running) {
        this.running.set(running);

        return this;
    }

    @Override
    public boolean isConnected() {
        return connected.get();
    }

    @Override
    public boolean getAndSetConnected(final boolean connected) {
        return this.connected.getAndSet(connected);
    }

    @Override
    public FileTransferState setConnected(final boolean connected) {
        this.connected.set(connected);

        return this;
    }

    @Override
    public String toString() {
        return toJson(true);
    }
}
