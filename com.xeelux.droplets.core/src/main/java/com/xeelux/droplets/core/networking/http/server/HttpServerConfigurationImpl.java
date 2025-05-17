package com.xeelux.droplets.core.networking.http.server;

import com.xeelux.droplets.core.networking.http.server.threading.ExecutorServiceProvider;

public class HttpServerConfigurationImpl implements HttpServerConfiguration {

	private String host = DEFAULT_HOST;
	private int port = DEFAULT_PORT;
	private int backlog = DEFAULT_BACKLOG;
	private ExecutorServiceProvider executorServiceProvider = DEFAULT_EXECUTOR_SERVICE_PROVIDER;

	private static final String DEFAULT_HOST = "127.0.0.1";
	private static final int DEFAULT_PORT = 8080;
	private static final int DEFAULT_BACKLOG = 256;
	private static final ExecutorServiceProvider DEFAULT_EXECUTOR_SERVICE_PROVIDER
			= ExecutorServiceProvider.getDefault();

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public HttpServerConfiguration setHost(final String host) {
		this.host = host;

		return this;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public HttpServerConfiguration setPort(final int port) {
		this.port = port < 1 ? DEFAULT_PORT : port;

		return this;
	}

	@Override
	public int getBacklog() {
		return backlog;
	}

	@Override
	public HttpServerConfiguration setBacklog(final int backlog) {
		this.backlog = backlog < 1 ? DEFAULT_BACKLOG : backlog;

		return this;
	}

	@Override
	public ExecutorServiceProvider getExecutorServiceProvider() {
		return executorServiceProvider;
	}

	@Override
	public HttpServerConfiguration setExecutorServiceProvider(final ExecutorServiceProvider executorServiceProvider) {
		this.executorServiceProvider = executorServiceProvider;

		return this;
	}

	@Override
	public String toString() {
		return toJson(true);
	}
}
