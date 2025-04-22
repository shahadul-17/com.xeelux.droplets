package com.xeelux.droplets.core.modules.filetransfer;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.io.MemoryOutputStream;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventArguments;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventListener;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventType;
import com.xeelux.droplets.core.utilities.CloseableUtilities;
import com.xeelux.droplets.core.utilities.CollectionUtilities;
import com.xeelux.droplets.core.utilities.StringUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileTransferConnectionImpl implements FileTransferConnection {

    private final Logger logger = LogManager.getLogger(FileTransferConnectionImpl.class);

    private final long connectionId;
    private String remoteHost;
    private int remotePort;

    private int maximumAllowedByteLength = DEFAULT_MAXIMUM_ALLOWED_BYTE_LENGTH;
    private int maximumAllowedReadAttempts = DEFAULT_MAXIMUM_ALLOWED_READ_ATTEMPTS;
    private final Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final MemoryOutputStream memoryOutputStream
            = MemoryOutputStream.create(MEMORY_OUTPUT_STREAM_INITIAL_CAPACITY);
    private final EventHandler<FileTransferEventArguments, FileTransferEventListener> eventHandler;

    private static final int MEMORY_OUTPUT_STREAM_INITIAL_CAPACITY = 8192;
    private static final int DEFAULT_MAXIMUM_ALLOWED_BYTE_LENGTH = 64 * 1024 * 1024;         // <-- 64 MB...
    private static final int DEFAULT_MAXIMUM_ALLOWED_READ_ATTEMPTS = 100;

    public FileTransferConnectionImpl(
            final long connectionId,
            final Socket socket,
            final EventHandler<FileTransferEventArguments, FileTransferEventListener> eventHandler) {
        this.connectionId = connectionId;
        this.socket = socket;
        this.eventHandler = eventHandler;
    }

    private InputStream tryGetInputStream(final Socket socket) {
        InputStream inputStream = null;

        try {
            inputStream = socket.getInputStream();
        } catch (final Throwable throwable) {
            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage("An exception occurred while getting the input stream of the client socket (" + remoteHost + ":" + remotePort + ").")
                    .setThrowable(throwable)
                    .executeEventListener(eventHandler);
        }

        return inputStream;
    }

    private void tryCloseInputStream(final InputStream inputStream) {
        if (inputStream == null) { return; }

        try {
            inputStream.close();
        } catch (final Throwable throwable) {
            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage("An exception occurred while closing the input stream of the client socket (" + remoteHost + ":" + remotePort + ").")
                    .setThrowable(throwable)
                    .executeEventListener(eventHandler);
        }
    }

    private OutputStream tryGetOutputStream(final Socket socket) {
        OutputStream outputStream = null;

        try {
            outputStream = socket.getOutputStream();
        } catch (final Throwable throwable) {
            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage("An exception occurred while getting the output stream of the client socket (" + remoteHost + ":" + remotePort + ").")
                    .setThrowable(throwable)
                    .executeEventListener(eventHandler);
        }

        return outputStream;
    }

    private void tryCloseOutputStream(final OutputStream outputStream) {
        if (outputStream == null) { return; }

        try {
            outputStream.close();
        } catch (final Throwable throwable) {
            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage("An exception occurred while closing the output stream of the client socket (" + remoteHost + ":" + remotePort + ").")
                    .setThrowable(throwable)
                    .executeEventListener(eventHandler);
        }
    }

    @Override
    public int getRemotePort() { return remotePort; }

    @Override
    public long getConnectionId() { return connectionId; }

    @Override
    public String getRemoteHost() { return remoteHost; }

    @Override
    public EventHandler<FileTransferEventArguments, FileTransferEventListener> getEventHandler() { return eventHandler; }

    @Override
    public int getMaximumAllowedByteLength() {
        return maximumAllowedByteLength;
    }

    @Override
    public FileTransferConnection setMaximumAllowedByteLength(final int maximumAllowedByteLength) {
        this.maximumAllowedByteLength = maximumAllowedByteLength < 1
                ? DEFAULT_MAXIMUM_ALLOWED_BYTE_LENGTH
                : maximumAllowedByteLength;

        return this;
    }

    @Override
    public int getMaximumAllowedReadAttempts() {
        return maximumAllowedReadAttempts;
    }

    @Override
    public FileTransferConnection setMaximumAllowedReadAttempts(final int maximumAllowedReadAttempts) {
        this.maximumAllowedReadAttempts = maximumAllowedReadAttempts < 1
                ? DEFAULT_MAXIMUM_ALLOWED_READ_ATTEMPTS
                : maximumAllowedReadAttempts;

        return this;
    }

    @Override
    public boolean initialize() {
        final var remoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        remoteHost = remoteSocketAddress.getHostName();
        remotePort = remoteSocketAddress.getPort();
        inputStream = tryGetInputStream(socket);

        // if the input stream is null...
        if (inputStream == null) {
            // we shall close the memory output stream...
            CloseableUtilities.tryClose(memoryOutputStream);

            // and we'll not proceed any further...
            return false;
        }

        outputStream = tryGetOutputStream(socket);

        // if the output stream is null...
        if (outputStream == null) {
            // we shall close the input stream...
            tryCloseInputStream(inputStream);
            // we shall close the memory output stream...
            CloseableUtilities.tryClose(memoryOutputStream);

            // assigning null to the input stream...
            inputStream = null;

            // and we'll not proceed any further...
            return false;
        }

        return true;
    }

    @Override
    public int tryRead(final byte[] buffer, final int offset, final int length) {
        if (inputStream == null) {
            final var message = "Failed to establish connection to the client.";

            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage(message)
                    .setThrowable(new Exception(message))
                    .executeEventListener(eventHandler);

            return -4;
        }

        try {
            return inputStream.read(buffer, offset, length);
        } catch (final SocketTimeoutException exception) {
            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage("Timeout exception occurred while reading from the input stream of the client socket ("
                            + connectionId + "@" + remoteHost + ":" + remotePort + ").")
                    .setThrowable(exception)
                    .executeEventListener(eventHandler);

            return -3;
        } catch (final Throwable throwable) {
            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage("An exception occurred while reading from the input stream of the client socket ("
                            + connectionId + "@" + remoteHost + ":" + remotePort + ").")
                    .setThrowable(throwable)
                    .executeEventListener(eventHandler);

            return -2;
        }
    }

    @Override
    public byte[] tryReadFixed(final int length) {
        // if length is less than zero, we shall return an empty byte array...
        if (length < 1) { return CollectionUtilities.getEmptyByteArray(); }
        // if length exceeds the maximum allowed length...
        if (length > getMaximumAllowedByteLength()) {
            final var message = "Fixed length read exceeds " + getMaximumAllowedByteLength() + " bytes.";

            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage(message)
                    .setThrowable(new IOException(message))
                    .executeEventListener(eventHandler);

            return CollectionUtilities.getEmptyByteArray();
        }

        final byte[] buffer = new byte[length];         // <-- allocating buffer...
        var bytesRead = 0;                              // <-- this holds the number of bytes read on each tryRead() method call...
        var totalBytesRead = 0;                         // <-- this holds the total number of bytes read...
        var bytesRemaining = 0;
        var readAttemptCount = 0;
        final var maximumAllowedReadAttempts = getMaximumAllowedReadAttempts();

        while (totalBytesRead < length) {
            if (++readAttemptCount > maximumAllowedReadAttempts) {
                final var message = "Maximum allowed read attempts exceeded while reading fixed number of bytes from client [" +
                        connectionId + "@" + remoteHost + ":" + remotePort + "].";

                FileTransferEventArguments.createInstance()
                        .setSender(this)
                        .setEventType(FileTransferEventType.EXCEPTION)
                        .setConnectionId(connectionId)
                        .setRemoteHost(remoteHost)
                        .setRemotePort(remotePort)
                        .setMessage(message)
                        .setThrowable(new IOException(message))
                        .executeEventListener(eventHandler);

                return CollectionUtilities.getEmptyByteArray();
            }

            bytesRemaining = length - totalBytesRead;
            bytesRead = tryRead(buffer, totalBytesRead, bytesRemaining);

            if (bytesRead < 0) {
                final var message = "Client [" + connectionId + "@" + remoteHost + ":" + remotePort
                        + "] got disconnected before the content could be fully read.";

                FileTransferEventArguments.createInstance()
                        .setSender(this)
                        .setEventType(FileTransferEventType.EXCEPTION)
                        .setConnectionId(connectionId)
                        .setRemoteHost(remoteHost)
                        .setRemotePort(remotePort)
                        .setMessage(message)
                        .setThrowable(new IOException(message))
                        .executeEventListener(eventHandler);

                return CollectionUtilities.getEmptyByteArray();
            }

            totalBytesRead += bytesRead;
        }

        return buffer;
    }

    @Override
    public Byte tryReadByte() {
        final var singleByteArray = tryReadFixed(1);        // <-- 1 byte...

        if (singleByteArray.length == 0) { return null; }

        final var byteValue = singleByteArray[0];

        return byteValue;
    }

    @Override
    public Integer tryReadInt32() {
        final var integerValueAsBytes = tryReadFixed(4);        // <-- int is of 4 bytes...

        if (integerValueAsBytes.length == 0) { return null; }

        final var integerValue = ByteBuffer.wrap(integerValueAsBytes).getInt();

        return integerValue;
    }

    @Override
    public Long tryReadInt64() {
        final var longValueAsBytes = tryReadFixed(8);        // <-- long is of 8 bytes...

        if (longValueAsBytes.length == 0) { return null; }

        final var longValue = ByteBuffer.wrap(longValueAsBytes).getLong();

        return longValue;
    }

    @Override
    public String tryReadString() {
        return tryReadString(StandardCharsets.UTF_8);
    }

    @Override
    public String tryReadString(final Charset charset) {
        if (charset == null) { return tryReadString(); }

        final var textLength = tryReadInt32();

        if (textLength == null) { return StringUtilities.getEmptyString(); }

        final var textAsBytes = tryReadFixed(textLength);
        final var text = new String(textAsBytes, 0, textAsBytes.length, charset);

        return text;
    }

    @Override
    public boolean tryWrite(final byte[] buffer) {
        return tryWrite(buffer, 0, buffer.length);
    }

    @Override
    public boolean tryWrite(final byte[] buffer, final int offset, final int length) {
        if (outputStream == null) {
            final var message = "Failed to establish connection to the client.";

            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage(message)
                    .setThrowable(new Exception(message))
                    .executeEventListener(eventHandler);

            return false;
        }

        try {
            // outputStream.write(buffer, offset, length);
            memoryOutputStream.write(buffer, offset, length);
        } catch (final Throwable throwable) {
            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage("An exception occurred while writing content to the buffer.")
                    .setThrowable(throwable)
                    .executeEventListener(eventHandler);

            return false;
        }

        return true;
    }

    @Override
    public boolean tryWriteByte(final byte value) {
        return tryWrite(new byte[] { value, });
    }

    @Override
    public boolean tryWriteInt32(final int value) {
        final var buffer = ByteBuffer
                .allocate(4)
                .putInt(value)
                .array();

        return tryWrite(buffer);
    }

    @Override
    public boolean tryWriteInt64(final long value) {
        final var buffer = ByteBuffer
                .allocate(8)
                .putLong(value)
                .array();

        return tryWrite(buffer);
    }

    @Override
    public boolean tryWriteString(final String text) {
        return tryWriteString(text, StandardCharsets.UTF_8);
    }

    @Override
    public boolean tryWriteString(final String text, final Charset charset) {
        if (StringUtilities.isNullOrEmpty(text)) { return false; }
        if (charset == null) { return tryWriteString(text); }

        final var buffer = text.getBytes(charset);
        final var textLength = buffer.length;

        // trying to write the text length...
        if (!tryWriteInt32(textLength)) { return false; }

        // writing the text...
        return tryWrite(buffer);
    }

    @Override
    public boolean tryFlush() {
        if (outputStream == null) {
            final var message = "Failed to establish connection to the client.";

            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage(message)
                    .setThrowable(new Exception(message))
                    .executeEventListener(eventHandler);

            return false;
        }

        try {
            // writing data from memory output stream to the client's output stream...
            memoryOutputStream.writeTo(outputStream);
            outputStream.flush();
        } catch (final Throwable throwable) {
            // if exception occurs, executing the event listener...
            FileTransferEventArguments.createInstance()
                    .setSender(this)
                    .setEventType(FileTransferEventType.EXCEPTION)
                    .setConnectionId(connectionId)
                    .setRemoteHost(remoteHost)
                    .setRemotePort(remotePort)
                    .setMessage("An exception occurred while flushing content to the output stream of the client socket ("
                            + connectionId + "@" + remoteHost + ":" + remotePort + ").")
                    .setThrowable(throwable)
                    .executeEventListener(eventHandler);

            return false;
        }

        return true;
    }
}
