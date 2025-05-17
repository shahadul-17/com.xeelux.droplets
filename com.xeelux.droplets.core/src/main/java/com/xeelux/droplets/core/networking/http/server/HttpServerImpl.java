package com.xeelux.droplets.core.networking.http.server;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.networking.http.server.event.HttpServerEventArguments;
import com.xeelux.droplets.core.networking.http.server.event.HttpServerEventHandlerImpl;
import com.xeelux.droplets.core.networking.http.server.event.HttpServerEventListener;
import com.xeelux.droplets.core.networking.http.server.event.HttpServerEventType;
import com.xeelux.droplets.core.utilities.CloseableUtilities;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServerImpl implements HttpServer, Runnable {

	private final Logger logger = LogManager.getLogger(HttpServerImpl.class);

	private Thread listenerThread = null;

	private final InetAddress address;
	private final HttpServerConfiguration configuration;
	private final HttpServerState state;
	private final EventHandler<HttpServerEventArguments, HttpServerEventListener> eventHandler
			= new HttpServerEventHandlerImpl();

	private static final HttpServerConfiguration DEFAULT_CONFIGURATION = new HttpServerConfigurationImpl();

	public HttpServerImpl() { this(DEFAULT_CONFIGURATION); }

	public HttpServerImpl(final HttpServerConfiguration configuration) {
		this.configuration = configuration;
		// preparing the address...
		address = new InetSocketAddress(this.configuration.getHost(), this.configuration.getPort()).getAddress();
		state = new HttpServerStateImpl();
	}

	@Override
	public HttpServerConfiguration getConfiguration() { return configuration; }

	@Override
	public EventHandler<HttpServerEventArguments, HttpServerEventListener> getEventHandler() { return eventHandler; }

	@Override
	public void start() throws RuntimeException {
		// checking if the server is already running and setting the running flag to true...
		// NOTE: WE ARE USING STATE OBJECT BECAUSE
		// WE WANT TO MAKE SURE THAT ALL THE OTHER THREADS
		// GET STOPPED AT ONCE...
		if (state.getAndSetRunning(true)) {
			logger.log(Level.WARN, "HTTP server is already running at http://{}:{}",
					configuration.getHost(), configuration.getPort());

			return;
		}

		// creating a new thread on which the server shall run...
		listenerThread = new Thread(this);
		listenerThread.setName("http-server@" + configuration.getHost() + ":" + configuration.getPort());
		listenerThread.start();
	}

	@Override
	public void stop() throws RuntimeException {
		// sets the running flag to false...
		// NOTE: WE ARE USING STATE OBJECT BECAUSE
		// WE WANT TO MAKE SURE THAT ALL THE OTHER THREADS
		// GET STOPPED AT ONCE...
		if (!state.getAndSetRunning(false)) {
			logger.log(Level.WARN, "HTTP server has already stopped.");

			return;
		}

		if (listenerThread == null) { return; }

		try {
			listenerThread.interrupt();
		} catch (final Throwable throwable) {
			final var message = "An exception occurred while interrupting the listener thread.";

			// if exception occurs, executing the event listener...
			HttpServerEventArguments.createInstance()
					.setSender(this)
					.setEventType(HttpServerEventType.EXCEPTION)
					.setMessage(message)
					.setException(new HttpServerExceptionImpl(this, message, throwable))
					.executeEventListener(eventHandler);
		}
	}

	@Override
	public void join() {
		if (listenerThread == null) { return; }

		try {
			listenerThread.join();
		} catch (final Throwable throwable) {
			final var message = "An exception occurred while waiting for the listener thread to finish.";

			// if exception occurs, executing the event listener...
			HttpServerEventArguments.createInstance()
					.setSender(this)
					.setEventType(HttpServerEventType.EXCEPTION)
					.setMessage(message)
					.setException(new HttpServerExceptionImpl(this, message, throwable))
					.executeEventListener(eventHandler);
		}
	}

	@Override
	public void close() throws IOException {
		stop();
	}

	@Override
	public void run() throws RuntimeException {
		ServerSocket serverSocket;

		try {
			// creating a server socket...
			serverSocket = new ServerSocket(configuration.getPort(), configuration.getBacklog(), address);
		} catch (final Throwable throwable) {
			final var message = "An exception occurred while binding the file transfer server on " + configuration.getHost() + ":" + configuration.getPort();

			// if exception occurs, executing the event listener...
			HttpServerEventArguments.createInstance()
					.setSender(this)
					.setEventType(HttpServerEventType.EXCEPTION)
					.setMessage(message)
					.setException(new HttpServerExceptionImpl(this, message, throwable))
					.executeEventListener(eventHandler);

			// we must stop the server (which sets running to false)...
			stop();

			// we shall not proceed any further...
			return;
		}

		// executing event listener...
		HttpServerEventArguments.createInstance()
				.setSender(this)
				.setEventType(HttpServerEventType.SERVER_START)
				.executeEventListener(eventHandler);

		var count = Long.MIN_VALUE;

		while (state.isRunning()) {
			Socket clientSocket;

			// executing event listener...
			HttpServerEventArguments.createInstance()
					.setSender(this)
					.setEventType(HttpServerEventType.SERVER_AWAITING_CONNECTION)
					.executeEventListener(eventHandler);

			try {
				// waiting for new client...
				clientSocket = serverSocket.accept();
			} catch (final Throwable throwable) {
				final var message = "An exception occurred while accepting new HTTP client.";

				// if exception occurs, executing the event listener...
				HttpServerEventArguments.createInstance()
						.setSender(this)
						.setEventType(HttpServerEventType.EXCEPTION)
						.setMessage(message)
						.setException(new HttpServerExceptionImpl(this, message, throwable))
						.executeEventListener(eventHandler);

				// we shall skip this iteration...
				continue;
			}

			// getting the current count as request ID...
			final var requestId = count;
			// incrementing the count...
			++count;

			// creating and starting a new connection handler...
			HttpRequestHandler
					.createInstance(requestId, clientSocket, this)
					.start();
		}

		// closing the server socket...
		CloseableUtilities.tryClose(serverSocket);
		// executing event listener...
		HttpServerEventArguments.createInstance()
				.setSender(this)
				.setEventType(HttpServerEventType.SERVER_STOP)
				.executeEventListener(eventHandler);
	}
}
