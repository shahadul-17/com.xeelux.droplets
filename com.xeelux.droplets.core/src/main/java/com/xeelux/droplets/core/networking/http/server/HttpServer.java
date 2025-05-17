package com.xeelux.droplets.core.networking.http.server;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.networking.http.server.event.HttpServerEventArguments;
import com.xeelux.droplets.core.networking.http.server.event.HttpServerEventListener;

import java.io.Closeable;

public interface HttpServer extends Closeable {
	HttpServerConfiguration getConfiguration();
	EventHandler<HttpServerEventArguments, HttpServerEventListener> getEventHandler();
	void start() throws RuntimeException;
	void stop() throws RuntimeException;
	void join() throws RuntimeException;
}
