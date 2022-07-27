package de.cau.testbed.server.util;

import de.cau.testbed.server.config.exception.PathTraversalException;

import java.io.File;
import java.io.IOException;
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

    public static Path sanitizeFileName(String fileName) throws PathTraversalException {
        final File file = new File(fileName);

        if (file.isAbsolute())
            throw new PathTraversalException("Given file name is absolute path");

        final String canonicalPath;
        final String absolutePath;

        try {
            canonicalPath = file.getCanonicalPath();
            absolutePath = file.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!canonicalPath.equals(absolutePath))
            throw new PathTraversalException("Canonical path and absolute path don't match");

        return file.toPath().getFileName();
    }
}
