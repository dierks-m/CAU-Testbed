package de.cau.testbed.server.network.fileTransfer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NodeTransferTarget implements TransferTarget {
    private final String host;
    private final String user;
    private final Path path;

    public NodeTransferTarget(String host, String user, Path path) {
        this.host = host;
        this.user = user;
        this.path = path;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public Path getPath() {
        return path;
    }
}
