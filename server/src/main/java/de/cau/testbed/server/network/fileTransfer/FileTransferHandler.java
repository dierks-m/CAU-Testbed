package de.cau.testbed.server.network.fileTransfer;

import de.cau.testbed.server.network.FirmwareRetrievalMessage;

import java.io.IOException;
import java.nio.file.Path;

public interface FileTransferHandler {
    void transfer(FirmwareRetrievalMessage retrievalMessage) throws IOException;
}
