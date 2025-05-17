package com.xeelux.droplets.core.networking.http.server;

import com.xeelux.droplets.core.networking.http.server.threading.ExecutorServiceProvider;
import com.xeelux.droplets.core.text.JsonSerializable;

public interface HttpServerConfiguration extends JsonSerializable {
	String getHost();
	HttpServerConfiguration setHost(final String host);

	int getPort();
	HttpServerConfiguration setPort(final int port);

	int getBacklog();
	HttpServerConfiguration setBacklog(final int backlog);

	ExecutorServiceProvider getExecutorServiceProvider();
	HttpServerConfiguration setExecutorServiceProvider(final ExecutorServiceProvider executorServiceProvider);
}
