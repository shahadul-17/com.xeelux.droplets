package com.xeelux.droplets.core.modules.filetransfer.server;

import com.xeelux.droplets.core.concurrency.ThreadSafeExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class FileTransferClientHandlerCollectionImpl implements FileTransferClientHandlerCollection {

    private final Logger logger = LogManager.getLogger(FileTransferClientHandlerCollectionImpl.class);
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);      // <-- this lock is used for thread synchronization...
    // NOTE: READ LOCK CAN BE ACQUIRED BY MULTIPLE THREADS SIMULTANEOUSLY
    // WHEN NO OTHER THREAD HAS ACQUIRED THE WRITE LOCK...
    private final Lock readLock = readWriteLock.readLock();
    // NOTE: WRITE LOCK CAN ONLY BE ACQUIRED BY A SINGLE THREAD...
    private final Lock writeLock = readWriteLock.writeLock();
    private final Map<Long, FileTransferClientHandler> fileTransferClientHandlerMap
            = new HashMap<>(FILE_TRANSFER_CLIENT_HANDLER_MAP_INITIAL_CAPACITY);

    private static final int FILE_TRANSFER_CLIENT_HANDLER_MAP_INITIAL_CAPACITY = 8192;

    @Override
    public FileTransferClientHandlerCollection put(
            final Long key,
            final FileTransferClientHandler clientHandler) {
        ThreadSafeExecutor.execute(writeLock, () -> fileTransferClientHandlerMap.put(key, clientHandler));

        return this;
    }

    @Override
    public FileTransferClientHandler get(final Long key) {
        return ThreadSafeExecutor.execute(readLock, () -> fileTransferClientHandlerMap.get(key));
    }

    @Override
    public FileTransferClientHandler remove(final Long key) {
        return ThreadSafeExecutor.execute(writeLock, () -> fileTransferClientHandlerMap.remove(key));
    }

    @Override
    public FileTransferClientHandlerCollection clear() {
        ThreadSafeExecutor.execute(writeLock, () -> {
            fileTransferClientHandlerMap.clear();

            return null;
        });

        return this;
    }
}
