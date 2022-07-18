package de.cau.testbed.server;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtil {
    private static final Path EXPERIMENTS_FOLDER = Paths.get("experiments");
    private static final Path FIRMWARE_FOLDER = Paths.get("firmware");
    private static Path workingDirectory;

    private PathUtil() {}

    public static void initialize(Path directory) {
        workingDirectory = directory;
    }

    public static Path getExperimentsPath() {
        return workingDirectory.resolve(EXPERIMENTS_FOLDER);
    }

    public static Path getExperimentPath(long experimentId) {
        return getExperimentsPath().resolve(Long.toString(experimentId));
    }

    public static Path getFirmwarePath(long experimentId) {
        return getExperimentPath(experimentId).resolve(FIRMWARE_FOLDER);
    }
}
