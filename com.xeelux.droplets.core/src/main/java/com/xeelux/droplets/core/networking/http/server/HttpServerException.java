package com.xeelux.droplets.core.networking.http.server;

public interface HttpServerException {
	HttpServer getHttpServer();
	String getMessage();
	String getStackTrace();
	Throwable getCause();
	Throwable asThrowable();
	Exception asException();
	RuntimeException asRuntimeException();
}
