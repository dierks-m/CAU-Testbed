package de.cau.testbed.server.service;

import de.cau.testbed.server.api.QueuedExperimentTemplate;
import de.cau.testbed.server.constants.UserType;
import de.cau.testbed.server.util.PathUtil;
import de.cau.testbed.server.api.AnonymizedExperimentInfo;
import de.cau.testbed.server.api.ExperimentTemplate;
import de.cau.testbed.server.config.HardwareNode;
import de.cau.testbed.server.config.datastore.Database;
import de.cau.testbed.server.config.exception.*;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.config.experiment.ExperimentModule;
import de.cau.testbed.server.config.experiment.ExperimentNode;
import de.cau.testbed.server.config.datastore.User;
import de.cau.testbed.server.constants.ExperimentStatus;
import de.cau.testbed.server.module.ExperimentSchedulingThread;
import jakarta.ws.rs.BadRequestException;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ExperimentService {
    private final Database database;
    private final List<HardwareNode> availableNodes;
    private final ExperimentSchedulingThread experimentScheduler;

    private static final Object DATABASE_LOCK = new Object();

    public ExperimentService(Database database, List<HardwareNode> availableNodes, ExperimentSchedulingThread experimentScheduler) {
        this.database = database;
        this.availableNodes = availableNodes;
        this.experimentScheduler = experimentScheduler;
    }

    public ExperimentDescriptor createNewExperiment(ExperimentTemplate template, User owner) throws TimeCollisionException, UnknownNodeException, UnknownModuleException {
        synchronized (DATABASE_LOCK) {
            checkTimeStamps(template);
            checkTimeCollision(template);
            checkModules(template);

            return database.addExperiment(template, owner);
        }
    }

    public ExperimentDescriptor queueNewExperiment(QueuedExperimentTemplate template, User owner) {
        synchronized (DATABASE_LOCK) {
            final LocalDateTime start = determineFreeTimeSlot(template.duration()).truncatedTo(ChronoUnit.SECONDS);

            System.out.println("Start " + start);
            System.out.println("Duration " + template.duration());

            return createNewExperiment(new ExperimentTemplate(
                    template.name(),
                    start,
                    start.plus(template.duration()),
                    template.nodes()
            ), owner);
        }
    }

    private LocalDateTime determineFreeTimeSlot(Duration duration) {
        final Duration durationWithBuffer = duration.plusMinutes(10);

        LocalDateTime previousTimestamp = LocalDateTime.now();
        Optional<ExperimentDescriptor> nextExperiment = database.getNextExperiment();

        while (nextExperiment.isPresent() && previousTimestamp.plus(durationWithBuffer).isAfter(nextExperiment.get().getStart())) {
            previousTimestamp = nextExperiment.get().getEnd();
            nextExperiment = database.getFollowingExperiment(nextExperiment.get());
        }

        return previousTimestamp.plusMinutes(5);
    }

    private void checkTimeStamps(ExperimentTemplate template) {
        if (template.end.isBefore(template.start))
            throw new TimeCollisionException("Experiment's start time is after end time");

        if (template.end.isBefore(LocalDateTime.now()))
            throw new TimeCollisionException("Experiment's end time is before current time");
    }

    private void checkTimeCollision(ExperimentTemplate template) throws TimeCollisionException {
        final List<ExperimentDescriptor> experimentsInTimeFrame = database.getExperimentsInTimeFrame(
                template.start.minusSeconds(299),
                template.end.plusSeconds(299)
        );

        if (experimentsInTimeFrame.stream().anyMatch(x -> !x.getStatus().isFinished()))
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

    public void scheduleExperiment(long id, User user) {
        final ExperimentDescriptor experiment = getAuthorizedExperimentById(id, user);

        synchronized (experiment.getLockObject()) {
            if (experiment.getStatus() != ExperimentStatus.CREATED)
                throw new BadRequestException("Experiment cannot be scheduled as it may already have been started");

            if (experiment.getEnd().compareTo(LocalDateTime.now()) < 0)
                throw new IllegalExperimentTimeException("Experiment's end time is before current time");

            checkExperimentFirmwareExists(experiment);

            experiment.setStatus(ExperimentStatus.SCHEDULED);
        }

        experimentScheduler.wakeup();
    }

    private void checkExperimentFirmwareExists(ExperimentDescriptor experiment) {
        final Set<String> firmwares = new HashSet<>();

        for (ExperimentNode node : experiment.getNodes()) {
            for (ExperimentModule module : node.modules) {
                firmwares.add(module.firmware);
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

    public List<AnonymizedExperimentInfo> listAnonymizedExperiments(LocalDateTime start, LocalDateTime end) {
        final List<ExperimentDescriptor> experimentDescriptors = database.getExperimentsInTimeFrame(start, end);

        final List<AnonymizedExperimentInfo> anonymizedDescriptors = new ArrayList<>();

        for (ExperimentDescriptor descriptor : experimentDescriptors) {
            anonymizedDescriptors.add(new AnonymizedExperimentInfo(descriptor.getName(), descriptor.getStart(), descriptor.getEnd(), descriptor.getId(), descriptor.getStatus()));
        }

        return anonymizedDescriptors;
    }

    public AnonymizedExperimentInfo cancelExperiment(long id, User user) {
        final ExperimentDescriptor experiment = getAuthorizedExperimentById(id, user);

        if (experiment.getStatus().isFinished())
            throw new RuntimeException("Experiment has already finished");

        experimentScheduler.cancelExperiment(experiment);

        return new AnonymizedExperimentInfo(experiment.getName(), experiment.getStart(), experiment.getEnd(), experiment.getId(), experiment.getStatus());
    }

    public AnonymizedExperimentInfo stopExperiment(long id, User user) {
        final ExperimentDescriptor experiment = getAuthorizedExperimentById(id, user);

        synchronized (experiment.getLockObject()) {
            if (experiment.getStatus().isFinished())
                throw new RuntimeException("Experiment has already finished");

            if (experiment.getStatus() == ExperimentStatus.STOPPING)
                throw new RuntimeException("Experiment is already stopping");

            if (!experiment.getStatus().hasStarted()) {
                experiment.setStatus(ExperimentStatus.CANCELLED);
            } else {
                experiment.setStatus(ExperimentStatus.STOPPING);
            }

            experimentScheduler.stopExperiment(experiment);

            return new AnonymizedExperimentInfo(experiment.getName(), experiment.getStart(), experiment.getEnd(), experiment.getId(), experiment.getStatus());
        }
    }

    public File createOrGetResultsFile(long id, User user) {
        final ExperimentDescriptor experiment = getAuthorizedExperimentById(id, user);

        if (!experiment.getStatus().isFinished())
            throw new RuntimeException("Experiment is not finished, yet");

        if (!PathUtil.getLogPath(id).toFile().isDirectory())
            throw new RuntimeException("No logs for experiment are present");

        final Path experimentDirectory = PathUtil.getExperimentPath(id);
        final File resultsZip = experimentDirectory.resolve("results_" + id + ".zip").toFile();

        if (!resultsZip.isFile())
            ZipUtil.pack(PathUtil.getLogPath(id).toFile(), resultsZip);

        return resultsZip;
    }

    private ExperimentDescriptor getAuthorizedExperimentById(long id, User user) {
        final Optional<ExperimentDescriptor> maybeExperiment = database.getExperimentById(id);

        if (maybeExperiment.isEmpty())
            throw new NoSuchExperimentException("Experiment does not exist");

        final ExperimentDescriptor experiment = maybeExperiment.get();

        if (!experiment.getOwner().equals(user) && !(user.getType() == UserType.ADMIN))
            throw new UnauthorizedException();

        return experiment;
    }
}
