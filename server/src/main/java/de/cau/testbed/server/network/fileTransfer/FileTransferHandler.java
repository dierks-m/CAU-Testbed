package de.cau.testbed.server.network.fileTransfer;

import de.cau.testbed.server.network.message.FirmwareRetrievalMessage;

import java.io.IOException;

public interface FileTransferHandler {
    void transfer(FirmwareRetrievalMessage retrievalMessage) throws IOException;
}
