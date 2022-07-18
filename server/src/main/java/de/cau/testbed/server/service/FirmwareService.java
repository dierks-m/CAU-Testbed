package de.cau.testbed.server.service;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FirmwareService {
    private final Path workingDirectory;

    public FirmwareService(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public void writeFile(InputStream uploadInputStream, long experimentId, String firmwareName) throws IOException {
        final Path target = Paths.get(workingDirectory.toString(), Long.toString(experimentId), "firmware", firmwareName);

        Files.copy(uploadInputStream, target);
    }
}
