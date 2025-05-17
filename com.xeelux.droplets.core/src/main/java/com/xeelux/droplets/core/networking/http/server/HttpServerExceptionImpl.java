package com.xeelux.droplets.core.networking.http.server;

import com.xeelux.droplets.core.utilities.ThrowableUtilities;

public class HttpServerExceptionImpl implements HttpServerException {

	private final String stackTrace;
	private final HttpServer httpServer;
	private final RuntimeException exception;

	public HttpServerExceptionImpl(final HttpServer httpServer) {
		this.httpServer = httpServer;
		exception = new RuntimeException();
		stackTrace = ThrowableUtilities.retrieveStackTrace(exception);
	}

	public HttpServerExceptionImpl(final HttpServer httpServer, final String message) {
		this.httpServer = httpServer;
		exception = new RuntimeException(message);
		stackTrace = ThrowableUtilities.retrieveStackTrace(exception);
	}

	public HttpServerExceptionImpl(
			final HttpServer httpServer,
			final String message,
			final Throwable cause) {
		this.httpServer = httpServer;
		exception = new RuntimeException(message, cause);
		stackTrace = ThrowableUtilities.retrieveStackTrace(exception);
	}

	@Override
	public HttpServer getHttpServer() { return httpServer; }

	@Override
	public String getMessage() {
		return exception.getMessage();
	}

	@Override
	public String getStackTrace() { return stackTrace; }

	@Override
	public Throwable getCause() { return exception.getCause(); }

	@Override
	public Throwable asThrowable() { return exception; }

	@Override
	public Exception asException() { return exception; }

	@Override
	public RuntimeException asRuntimeException() { return exception; }
}
