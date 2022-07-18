package de.cau.testbed.server.service;

import de.cau.testbed.server.PathUtil;
import de.cau.testbed.server.api.ExperimentTemplate;
import de.cau.testbed.server.config.HardwareNode;
import de.cau.testbed.server.config.datastore.Database;
import de.cau.testbed.server.config.exception.*;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.config.experiment.ExperimentModule;
import de.cau.testbed.server.config.experiment.ExperimentNode;
import de.cau.testbed.server.constants.ExperimentStatus;
import de.cau.testbed.server.module.ExperimentSchedulingThread;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class ExperimentService {
    private final Database database;
    private final List<HardwareNode> availableNodes;
    private final ExperimentSchedulingThread experimentScheduler;
    private final Path workingDirectory;

    public ExperimentService(Database database, List<HardwareNode> availableNodes, ExperimentSchedulingThread experimentScheduler, Path workingDirectory) {
        this.database = database;
        this.availableNodes = availableNodes;
        this.experimentScheduler = experimentScheduler;
        this.workingDirectory = workingDirectory;
    }

    public long createNewExperiment(ExperimentTemplate template) throws TimeCollisionException, UnknownNodeException, UnknownModuleException {
        checkTimeCollision(template);
        checkModules(template);

        final ExperimentDescriptor experiment = database.addExperiment(template);

        return experiment.getId();
    }

    private void checkTimeCollision(ExperimentTemplate template) throws TimeCollisionException {
        if (!database.getExperimentsInTimeFrame(template.start.minusMinutes(5), template.end.plusMinutes(5)).isEmpty())
            throw new TimeCollisionException("Experiment's start or end time is too close to another experiment");
    }

    private void checkModules(ExperimentTemplate template) throws UnknownModuleException, UnknownNodeException {
        for (ExperimentNode node : template.nodes) {
            assertHardwareNodeExists(node);
        }
    }

    private void assertHardwareNodeExists(ExperimentNode node) throws UnknownModuleException, UnknownNodeException {
        for (HardwareNode hardwareNode : availableNodes) {
            if (hardwareNode.id.equals(node.id)) {
                for (ExperimentModule module : node.modules) {
                    if (!hardwareNode.capabilities.contains(module.moduleType))
                        throw new UnknownModuleException("Module type " + module.moduleType + " is not supported by " + hardwareNode.id);
                    else
                        return;
                }
            }
        }

        throw new UnknownNodeException("No node called " + node.id + " exists");
    }

    public void scheduleExperiment(long id) {
        final Optional<ExperimentDescriptor> maybeExperiment = database.getExperimentById(id);

        if (maybeExperiment.isEmpty())
            throw new NoSuchExperimentException("Experiment does not exist");

        final ExperimentDescriptor experiment = maybeExperiment.get();

        if (LocalDateTime.now().compareTo(experiment.getEnd()) >= 0)
            throw new IllegalExperimentTimeException("Experiment's end time is before current time");

        checkExperimentFirmwareExists(experiment);

        experiment.setStatus(ExperimentStatus.SCHEDULED);
        database.updateExperiment(experiment);
        experimentScheduler.wakeup();
    }

    private void checkExperimentFirmwareExists(ExperimentDescriptor experiment) {
        final Set<String> firmwares = new HashSet<>();

        for (ExperimentNode node : experiment.getNodes()) {
            for (ExperimentModule module : node.modules) {
                firmwares.add(module.firmwarePath);
            }
        }

        for (String firmware : firmwares) {
            assertFirmwareExists(experiment.getId(), firmware);
        }
    }

    private void assertFirmwareExists(long experimentId, String firmware) {
        if (!Files.isRegularFile(PathUtil.getFirmwarePath(experimentId).resolve(firmware)))
            throw new FirmwareDoesNotExistException("Firmware " + firmware + " is not present");
    }
}
