package com.xeelux.droplets.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class MemoryInputStreamImpl extends InputStream implements MemoryInputStream {

	private boolean closed = false;
	private byte[] buffer;
	private int currentPosition = 0;
	private int length;

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	private static final String EMPTY_STRING = "";

	MemoryInputStreamImpl(final byte[] buffer) throws IllegalArgumentException {
		if (buffer == null) { throw new IllegalArgumentException("Provided buffer is null."); }

		this.buffer = buffer;
		this.length = buffer.length;
	}

	protected boolean isClosed() { return closed; }

	protected byte[] getBuffer() { return buffer; }

	protected void setBuffer(final byte[] buffer) {
		this.buffer = buffer;
	}

	protected int getCurrentPosition() { return currentPosition; }

	protected void setCurrentPosition(final int currentPosition) {
		this.currentPosition = currentPosition;
	}

	protected int getLength() { return length; }

	protected void setLength(final int length) {
		this.length = length;
	}

	protected void throwExceptionIfClosed() throws IOException {
		if (!isClosed()) { return; }

		throw new IOException("Stream is closed.");
	}

	@Override
	public int length() throws IOException {
		throwExceptionIfClosed();

		return getLength();
	}

	@Override
	public int available() throws IOException {
		throwExceptionIfClosed();

		final var length = getLength();
		final var currentPosition = getCurrentPosition();

		return length - currentPosition;
	}

	@Override
	public int position() throws IOException {
		throwExceptionIfClosed();

		return getCurrentPosition();
	}

	@Override
	public void setPosition(final int position) throws IllegalArgumentException, IOException {
		throwExceptionIfClosed();

		final var length = getLength();

		if (position < 0 || position > length) {
			throw new IllegalArgumentException("Invalid position: " + position);
		}

		setCurrentPosition(position);
	}

	@Override
	public void resetPosition() throws IOException {
		throwExceptionIfClosed();
		setPosition(0);
	}

	@Override
	public byte[] readBytes(final int length) throws IllegalArgumentException, IOException {
		throwExceptionIfClosed();

		if (length < 0) { throw new IllegalArgumentException("Length cannot be negative."); }
		if (length == 0) { return EMPTY_BYTE_ARRAY; }

		final var available = available();

		if (length > available) {
			throw new IllegalArgumentException("Requested length exceeds the available bytes.");
		}

		final var bytes = new byte[length];
		final var buffer = getBuffer();
		final var currentPosition = getCurrentPosition();

		System.arraycopy(buffer, currentPosition, bytes, 0, length);
		setCurrentPosition(currentPosition + length);

		return bytes;
	}

	@Override
	public byte readByte() throws IOException {
		throwExceptionIfClosed();

		final var available = available();

		if (available < 1) { return -1; }

		final var buffer = getBuffer();
		final var currentPosition = getCurrentPosition();
		final var byteValue = buffer[currentPosition];

		setCurrentPosition(currentPosition + 1);

		return byteValue;
	}

	@Override
	public int read() throws IOException {
		final var byteValue = readByte();

		return byteValue & 0xFF;
	}

	@Override
	public String readString(final int length) throws IllegalArgumentException, IOException {
		return readString(length, StandardCharsets.UTF_8);
	}

	@Override
	public String readString(final int length, final Charset charset) throws IllegalArgumentException, IOException {
		throwExceptionIfClosed();

		if (charset == null) { throw new IllegalArgumentException("Provided charset is null."); }
		if (length == 0) { return EMPTY_STRING; }

		final byte[] bytes = readBytes(length);

		return new String(bytes, charset);
	}

	@Override
	public InputStream asInputStream() { return this; }

	@Override
	public byte[] getInternalBuffer() throws IOException {
		throwExceptionIfClosed();

		return buffer;
	}

	@Override
	public void close() throws IOException {
		if (isClosed()) { return; }

		currentPosition = 0;
		closed = true;
	}
}
