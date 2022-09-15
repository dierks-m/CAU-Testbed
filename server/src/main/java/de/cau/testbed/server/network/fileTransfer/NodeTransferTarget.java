package de.cau.testbed.server.network.fileTransfer;

import java.nio.file.Path;

public record NodeTransferTarget(String host, String user, Path path) implements TransferTarget {
}
