package de.cau.testbed.server.service;


import de.cau.testbed.server.PathUtil;
import de.cau.testbed.server.config.datastore.Database;
import de.cau.testbed.server.config.datastore.User;
import de.cau.testbed.server.config.exception.NoSuchExperimentException;
import de.cau.testbed.server.config.exception.PathTraversalException;
import de.cau.testbed.server.config.exception.UnauthorizedException;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FirmwareService {
    private final Database database;

    public FirmwareService(Database database) {
        this.database = database;
    }

    public void writeFile(InputStream uploadInputStream, long experimentId, String firmwareName) throws PathTraversalException, IOException {
        // Will prevent upwards
        final Path safeFirmwarePath = PathUtil.sanitizeFileName(firmwareName);

        Files.createDirectories(PathUtil.getFirmwarePath(experimentId));
        final Path target = PathUtil.getFirmwarePath(experimentId).resolve(safeFirmwarePath);

        Files.copy(uploadInputStream, target);
    }

    public void authorizeUserForExperiment(User user, long experimentId) {
        final Optional<ExperimentDescriptor> maybeExperimentDescriptor = database.getExperimentById(experimentId);

        if (maybeExperimentDescriptor.isEmpty())
            throw new NoSuchExperimentException("Experiment with id " + experimentId + " does not exist");

        if (!maybeExperimentDescriptor.get().getOwner().equals(user))
            throw new UnauthorizedException();
    }
}
