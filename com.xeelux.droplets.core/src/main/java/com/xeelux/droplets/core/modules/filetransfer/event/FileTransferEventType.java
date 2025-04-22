package com.xeelux.droplets.core.modules.filetransfer.event;

public enum FileTransferEventType {
    NONE,
    SERVER_START,
    SERVER_AWAITING_CONNECTION,
    SERVER_STOP,
    CLIENT_CONNECT,
    CLIENT_DISCONNECT,
    BEFORE_CONTENT_SEND,
    CONTENT_SEND,
    CONTENT_RECEIVE,
    EXCEPTION,
}
