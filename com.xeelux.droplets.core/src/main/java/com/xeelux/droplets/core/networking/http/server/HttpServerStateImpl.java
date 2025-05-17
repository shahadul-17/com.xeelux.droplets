package com.xeelux.droplets.core.networking.http.server;

import com.xeelux.droplets.core.concurrency.ThreadSafeBoolean;

class HttpServerStateImpl implements HttpServerState {

    private final ThreadSafeBoolean running = new ThreadSafeBoolean();
    private final ThreadSafeBoolean connected = new ThreadSafeBoolean();

    @Override
    public boolean isRunning() { return running.get(); }

    @Override
    public boolean getAndSetRunning(final boolean running) {
        return this.running.getAndSet(running);
    }

    @Override
    public HttpServerState setRunning(final boolean running) {
        this.running.set(running);

        return this;
    }

    @Override
    public boolean isConnected() { return connected.get(); }

    @Override
    public boolean getAndSetConnected(final boolean connected) {
        return this.connected.getAndSet(connected);
    }

    @Override
    public HttpServerState setConnected(final boolean connected) {
        this.connected.set(connected);

        return this;
    }

    @Override
    public String toString() {
        return toJson(true);
    }
}
