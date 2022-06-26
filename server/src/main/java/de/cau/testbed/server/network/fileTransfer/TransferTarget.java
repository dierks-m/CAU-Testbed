package de.cau.testbed.server.network.fileTransfer;

import java.nio.file.Path;

public interface TransferTarget {
    String getHost();
    String getUser();
    Path getPath();
}
