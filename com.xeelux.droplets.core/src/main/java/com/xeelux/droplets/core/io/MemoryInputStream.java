package com.xeelux.droplets.core.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * MemoryInputStream is a memory-backed InputStream implementation
 * that supports reading bytes, byte arrays, and strings (with charset)
 * from an internal buffer.
 *
 * <p>
 * This interface provides an abstraction for working with in-memory
 * input streams without exposing the internal buffer for modification.
 * </p>
 *
 * <p><strong>Note:</strong> Implementations are not thread-safe unless otherwise specified.</p>
 *
 * @author Md. Shahadul Alam Patwary
 * @since 2025-04-28
 * @version 1.0
 */
public interface MemoryInputStream extends Closeable {

	int length() throws IOException;
	int available() throws IOException;
	int position() throws IOException;
	void setPosition(final int position) throws IllegalArgumentException, IOException;
	void resetPosition() throws IOException;
	byte[] readBytes(final int length) throws IllegalArgumentException, IOException;
	byte readByte() throws IOException;
	int read() throws IOException;
	int read(final byte[] buffer, final int offset, final int length) throws IOException;
	String readString(final int length) throws IllegalArgumentException, IOException;
	String readString(final int length, final Charset charset) throws IllegalArgumentException, IOException;
	InputStream asInputStream();

	/**
	 * Gets the internal buffer.
	 * Note: The returned byte array should be treated as read-only.
	 * @return The internal buffer.
	 * @throws IOException If exception occurs while getting the internal buffer.
	 */
	byte[] getInternalBuffer() throws IOException;

	static MemoryInputStream create(final byte[] buffer) throws IllegalArgumentException {
		return new MemoryInputStreamImpl(buffer);
	}
}
