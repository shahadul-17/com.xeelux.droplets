package com.xeelux.droplets.core.networking.http.server.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPerTaskExecutorServiceProvider implements ExecutorServiceProvider {

	private ExecutorService executorService;

	@Override
	public ExecutorService getExecutorService() {
		if (executorService != null) { return executorService; }

		executorService = Executors.newThreadPerTaskExecutor(Thread.ofVirtual()
				.name("virtual-", 1L)
				.factory());

		return executorService;
	}
}
