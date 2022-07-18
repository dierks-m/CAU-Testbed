package de.cau.testbed.server.service;


import de.cau.testbed.server.PathUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FirmwareService {
    private final Path workingDirectory;

    public FirmwareService(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public void writeFile(InputStream uploadInputStream, long experimentId, String firmwareName) throws IOException {
        Files.createDirectories(PathUtil.getFirmwarePath(experimentId));
        final Path target = PathUtil.getFirmwarePath(experimentId).resolve(firmwareName);

        Files.copy(uploadInputStream, target);
    }
}
