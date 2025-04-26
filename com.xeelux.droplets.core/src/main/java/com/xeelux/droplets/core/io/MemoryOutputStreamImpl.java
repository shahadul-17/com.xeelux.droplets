package com.xeelux.droplets.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class MemoryOutputStreamImpl extends OutputStream implements MemoryOutputStream {

    private boolean closed = false;
    private byte[] buffer;
    private int currentPosition = 0;
    private int currentCapacity;
    private int initialCapacity;

    private static final int DEFAULT_INITIAL_CAPACITY = 64;
    private static final String EMPTY_STRING = "";

    MemoryOutputStreamImpl() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    MemoryOutputStreamImpl(final int initialCapacity) {
        this.initialCapacity = initialCapacity < 1
                ? DEFAULT_INITIAL_CAPACITY
                : initialCapacity;
        // initializing the buffer...
        buffer = new byte[this.initialCapacity];
        currentCapacity = this.initialCapacity;
    }

    protected boolean isClosed() { return closed; }

    protected void setClosed(final boolean closed) {
        this.closed = closed;
    }

    protected byte[] getBuffer() { return buffer; }

    protected void setBuffer(final byte[] buffer) {
        this.buffer = buffer;
    }

    protected int getCurrentPosition() { return currentPosition; }

    protected void setCurrentPosition(final int currentPosition) {
        this.currentPosition = currentPosition;
    }

    protected int getCurrentCapacity() { return currentCapacity; }

    protected void setCurrentCapacity(final int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    protected void throwExceptionIfClosed() throws IOException {
        if (!isClosed()) { return; }

        throw new IOException("Stream is closed.");
    }

    protected void ensureCapacity(final int minimumCapacity) {
        final var currentCapacity = getCurrentCapacity();

        if (minimumCapacity <= currentCapacity) { return; }

        final var currentBuffer = getBuffer();
        final var currentPosition = getCurrentPosition();
        final var newCapacity = Math.max(currentCapacity * 2, minimumCapacity);
        final var newBuffer = new byte[newCapacity];

        System.arraycopy(currentBuffer, 0, newBuffer, 0, currentPosition);
        setBuffer(newBuffer);
        setCurrentCapacity(newCapacity);
    }

    @Override
    public int getInitialCapacity() throws IOException {
        throwExceptionIfClosed();

        return initialCapacity;
    }

    @Override
    public void write(final byte byteValue) throws IOException {
        throwExceptionIfClosed();

        // getting the current position...
        final var currentPosition = getCurrentPosition();
        // incrementing the current position of the buffer...
        final var incrementedCurrentPosition = currentPosition + 1;

        // this method makes sure that the buffer has enough capacity.
        // if not, this will resize the buffer...
        ensureCapacity(incrementedCurrentPosition);

        // retrieving the global buffer...
        // NOTE: THIS METHOD MUST BE CALLED AFTER CALLING ensureCapacity() METHOD...!!!
        final var globalBuffer = getBuffer();
        // setting the given byte value to the global buffer...
        globalBuffer[currentPosition] = byteValue;

        // assigning the incremented current position as the current position...
        setCurrentPosition(incrementedCurrentPosition);
    }

    @Override
    public void write(final int byteValue) throws IOException {
        write((byte) byteValue);
    }

    @Override
    public void write(final byte[] buffer) throws IllegalArgumentException, IOException {
        write(buffer, 0, buffer.length);
    }

    @Override
    public void write(final byte[] buffer, final int offset, final int length) throws IllegalArgumentException, IOException {
        throwExceptionIfClosed();

        // if buffer is null, we shall throw exception...
        if (buffer == null) { throw new IllegalArgumentException("Provided buffer is null."); }
        // if offset is less than zero (0) or, the length is less than zero (0),
        // or the summation of the offset and the length exceeds the buffer length,
        // we shall throw exception...
        if (offset < 0 || length < 0 || offset + length > buffer.length) {
            throw new IllegalArgumentException("Invalid offset or length provided.");
        }

        // if length is zero (0), we shall not proceed any further...
        if (length == 0) { return; }

        // getting the current position...
        final var currentPosition = getCurrentPosition();
        // incrementing the current position of the buffer...
        final var incrementedCurrentPosition = currentPosition + length;

        // this method makes sure that the buffer has enough capacity.
        // if not, this will resize the buffer...
        ensureCapacity(incrementedCurrentPosition);

        // retrieving the global buffer...
        // NOTE: THIS METHOD MUST BE CALLED AFTER CALLING ensureCapacity() METHOD...!!!
        final var globalBuffer = getBuffer();

        // copying contents from the given buffer to the global buffer...
        System.arraycopy(buffer, offset, globalBuffer, currentPosition, length);
        // assigning the incremented current position as the current position...
        setCurrentPosition(incrementedCurrentPosition);
    }

    @Override
    public void write(final String text, final Charset charset) throws IllegalArgumentException, IOException {
        throwExceptionIfClosed();

        // if text is null or empty, we shall not proceed any further...
        if (text == null || text.isEmpty()) { return; }

        final var buffer = text.getBytes(charset);

        write(buffer);
    }

    @Override
    public void write(final String text) throws IllegalArgumentException, IOException {
        write(text, StandardCharsets.UTF_8);
    }

    @Override
    public void writeTo(final OutputStream outputStream) throws IllegalArgumentException, IOException {
        final var currentPosition = getCurrentPosition();

        writeTo(outputStream, 0, currentPosition);
    }

    @Override
    public void writeTo(final OutputStream outputStream, final int offset, final int length) throws IllegalArgumentException, IOException {
        throwExceptionIfClosed();

        // if output stream is null, we shall throw exception...
        if (outputStream == null) { throw new IllegalArgumentException("Provided output stream is null."); }

        final int currentPosition = getCurrentPosition();

        // if offset is less than zero (0) or, the length is less than zero (0),
        // or the summation of the offset and the length exceeds the current position (which is actually the length),
        // we shall throw exception...
        if (offset < 0 || length < 0 || offset + length > currentPosition) {
            throw new IllegalArgumentException("Invalid offset or length provided.");
        }

        // if length is zero (0), we shall not proceed any further...
        if (length == 0) { return; }

        final var buffer = getBuffer();

        outputStream.write(buffer, offset, length);
    }

    @Override
    public int length() throws IOException {
        throwExceptionIfClosed();

        return getCurrentPosition();
    }

    @Override
    public int capacity() throws IOException {
        throwExceptionIfClosed();

        return getCurrentCapacity();
    }

    @Override
    public void clear() throws IOException {
        throwExceptionIfClosed();
        setCurrentPosition(0);
    }

    @Override
    public void trimToSize() throws IOException {
        throwExceptionIfClosed();

        final var currentPosition = getCurrentPosition();
        final var currentCapacity = getCurrentCapacity();

        // if current position is equal to the current capacity,
        // we don't need to trim...
        if (currentPosition == currentCapacity) { return; }

        final var currentBuffer = getBuffer();
        final var trimmedBuffer = new byte[currentPosition];

        System.arraycopy(currentBuffer, 0, trimmedBuffer, 0, currentPosition);

        setBuffer(trimmedBuffer);
        setCurrentCapacity(currentPosition);
    }

    @Override
    public void flush() throws IOException {
        throwExceptionIfClosed();
    }

    @Override
    public void close() throws IOException {
        // if this instance is already closed, we shall do nothing...
        if (isClosed()) { return; }

        initialCapacity = 0;

        clear();
        setCurrentCapacity(0);
        setBuffer(null);            // <-- NOTE: ALWAYS NULLIFY THE BUFFER AFTER ALL THE OTHER METHOD CALLS AS THEY MIGHT INTERNALLY USE THE BUFFER...!!!
        setClosed(true);
    }

    @Override
    public OutputStream asOutputStream() { return this; }

    @Override
    public byte[] getInternalBuffer() throws IOException {
        throwExceptionIfClosed();

        return getBuffer();
    }

    @Override
    public byte[] getBytes() throws IOException {
        throwExceptionIfClosed();

        final var currentPosition = getCurrentPosition();
        final var currentBuffer = getBuffer();
        final var bytes = new byte[currentPosition];

        System.arraycopy(currentBuffer, 0, bytes, 0, currentPosition);

        return bytes;
    }

    @Override
    public String toString(final Charset charset) {
        // if this instance is closed, we shall return an empty string...
        if (isClosed()) { return EMPTY_STRING; }

        // using the protected method getCurrentPosition() instead of length() method
        // because this toString() method does not throw IO exception...
        final var length = getCurrentPosition();

        // if length is less than one (1), we shall return an empty string...
        if (length < 1) { return EMPTY_STRING; }

        final var bytes = getBuffer();

        return new String(bytes, 0, length, charset);
    }

    @Override
    public String toString() {
        return toString(StandardCharsets.UTF_8);
    }
}
