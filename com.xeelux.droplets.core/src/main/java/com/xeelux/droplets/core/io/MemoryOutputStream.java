package com.xeelux.droplets.core.io;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * MemoryOutputStream is a dynamically growing in-memory OutputStream
 * implementation that supports writing primitive bytes, byte arrays,
 * strings (with charset), and outputting to other streams or buffers.
 *
 * <p>
 * This interface provides an abstraction for working with memory-backed
 * output streams without exposing the internal buffer for modification.
 * </p>
 *
 * <p><strong>Note:</strong> Implementations are not thread-safe unless otherwise specified.</p>
 *
 * @author Md. Shahadul Alam Patwary
 * @since 2025-04-26
 * @version 1.0
 */
public interface MemoryOutputStream extends Flushable, Closeable {

    int getInitialCapacity() throws IOException;
    void write(final byte byteValue) throws IOException;
    void write(final int byteValue) throws IOException;
    void write(final byte[] buffer) throws IllegalArgumentException, IOException;
    void write(final byte[] buffer, final int offset, final int length) throws IllegalArgumentException, IOException;
    void write(final String text, final Charset charset) throws IllegalArgumentException, IOException;
    void write(final String text) throws IllegalArgumentException, IOException;
    void writeTo(final OutputStream outputStream) throws IllegalArgumentException, IOException;
    void writeTo(final OutputStream outputStream, final int offset, final int length) throws IllegalArgumentException, IOException;
    int length() throws IOException;
    int capacity() throws IOException;
    void clear() throws IOException;
    void trimToSize() throws IOException;
    OutputStream asOutputStream();

    /**
     * Gets the internal buffer.
     * Note: The returned byte array may be larger than the data written and
     * should be treated as read-only. Do not modify the contents.
     * @return The internal buffer.
     * @throws IOException If exception occurs while getting the internal buffer.
     */
    byte[] getInternalBuffer() throws IOException;
    byte[] getBytes() throws IOException;
    String toString(final Charset charset);

    default byte[] toByteArray() throws IOException {
        return getBytes();
    }

    default void writeTo(final MemoryOutputStream outputStream) throws IllegalArgumentException, IOException {
        writeTo(outputStream.asOutputStream());
    }

    default void writeTo(
            final MemoryOutputStream outputStream,
            final int offset,
            final int length) throws IllegalArgumentException, IOException {
        // if output stream is null, we shall throw exception...
        if (outputStream == null) { throw new IllegalArgumentException("Provided output stream is null."); }

        writeTo(outputStream.asOutputStream(), offset, length);
    }

    static MemoryOutputStream create() {
        return new MemoryOutputStreamImpl();
    }

    static MemoryOutputStream create(final int initialCapacity) {
        return new MemoryOutputStreamImpl(initialCapacity);
    }
}
