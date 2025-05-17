package com.xeelux.droplets.core.networking.http.server.threading;

import java.util.concurrent.ExecutorService;

public interface ExecutorServiceProvider {
	ExecutorService getExecutorService();

	static ExecutorServiceProvider getDefault() {
		return DefaultExecutorServiceProvider.getInstance();
	}
}
