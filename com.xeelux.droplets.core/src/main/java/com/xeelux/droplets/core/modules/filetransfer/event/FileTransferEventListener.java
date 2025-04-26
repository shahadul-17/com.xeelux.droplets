package com.xeelux.droplets.core.modules.filetransfer.event;

public interface FileTransferEventListener {
    default void onClientStart(final FileTransferEventArguments eventArguments) { }
    default void onClientStop(final FileTransferEventArguments eventArguments) { }
    default void onServerStart(final FileTransferEventArguments eventArguments) { }
    default void onServerAwaitingConnection(final FileTransferEventArguments eventArguments) { }
    default void onServerStop(final FileTransferEventArguments eventArguments) { }
    default void onClientConnect(final FileTransferEventArguments eventArguments) { }
    default void onClientDisconnect(final FileTransferEventArguments eventArguments) { }
    default void onBeforeContentSend(final FileTransferEventArguments eventArguments) { }
    default void onContentSend(final FileTransferEventArguments eventArguments) { }
    default void onContentReceive(final FileTransferEventArguments eventArguments) { }
    default void onException(final FileTransferEventArguments eventArguments) { }
}
