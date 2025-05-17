package com.xeelux.droplets.core.networking.http.server.threading;

class DefaultExecutorServiceProvider extends ThreadPerTaskExecutorServiceProvider {

	private static final ExecutorServiceProvider executorServiceProvider
			= new DefaultExecutorServiceProvider();

	private DefaultExecutorServiceProvider() { }

	static ExecutorServiceProvider getInstance() { return executorServiceProvider; }
}
