package com.xeelux.droplets.core.modules.filetransfer;

import com.xeelux.droplets.core.common.EventHandler;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventArguments;
import com.xeelux.droplets.core.modules.filetransfer.event.FileTransferEventListener;

import java.nio.charset.Charset;

public interface FileTransferConnection {

    int getRemotePort();
    long getConnectionId();
    String getRemoteHost();
    EventHandler<FileTransferEventArguments, FileTransferEventListener> getEventHandler();

    int getMaximumAllowedByteLength();
    FileTransferConnection setMaximumAllowedByteLength(final int maximumAllowedLength);

    int getMaximumAllowedReadAttempts();
    FileTransferConnection setMaximumAllowedReadAttempts(final int maximumAllowedReadAttempts);

    boolean initialize();

    int tryRead(final byte[] buffer, final int offset, final int length);
    byte[] tryReadFixed(final int length);
    Byte tryReadByte();
    Integer tryReadInt32();
    Long tryReadInt64();
    String tryReadString();
    String tryReadString(final Charset charset);

    boolean tryWrite(final byte[] buffer);
    boolean tryWrite(final byte[] buffer, final int offset, final int length);
    boolean tryWriteByte(final byte value);
    boolean tryWriteInt32(final int value);
    boolean tryWriteInt64(final long value);
    boolean tryWriteString(final String text);
    boolean tryWriteString(final String text, final Charset charset);
    boolean tryFlush();
}
