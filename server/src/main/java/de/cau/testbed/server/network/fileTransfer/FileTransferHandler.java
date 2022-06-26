package de.cau.testbed.server.network.fileTransfer;

import de.cau.testbed.server.network.message.FirmwareRetrievalMessage;

import java.io.IOException;
import java.nio.file.Path;

public interface FileTransferHandler {
    void upload(TransferTarget target, Path localPath) throws IOException;
    void download(TransferTarget target, Path localPath) throws IOException;
}
