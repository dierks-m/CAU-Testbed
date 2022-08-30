package de.cau.testbed.server.config.datastore.yaml;

import de.cau.testbed.server.util.PathUtil;
import de.cau.testbed.server.api.ExperimentTemplate;
import de.cau.testbed.server.config.YAMLParser;
import de.cau.testbed.server.config.datastore.Database;
import de.cau.testbed.server.config.datastore.UserDatabase;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.config.experiment.ExperimentDetail;
import de.cau.testbed.server.config.datastore.User;
import de.cau.testbed.server.constants.ExperimentStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class YAMLDatabase implements Database {
    private final Path workingDirectory;
    private final YAMLUserDatabase userDatabase;

    private long nextId;
    private final List<ExperimentDescriptor> experimentDescriptors;

    public YAMLDatabase(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
        final YAMLExperimentList experimentList = loadExperimentList();
        this.userDatabase = new YAMLUserDatabase(workingDirectory);
        this.nextId = experimentList.nextId;
        this.experimentDescriptors = loadExperiments(experimentList);
    }

    private YAMLUserTable loadUserTable() {
        try {
            return YAMLParser.parseFile(workingDirectory.resolve("users.yaml"), YAMLUserTable.class);
        } catch (IOException e) {
            return new YAMLUserTable(Collections.emptyList(), 1);
        }
    }

    private YAMLExperimentList loadExperimentList() {
        try {
            return YAMLParser.parseFile(workingDirectory.resolve("experiments.yaml"), YAMLExperimentList.class);
        } catch (IOException e) {
            return new YAMLExperimentList(Collections.emptyList(), 1);
        }
    }

    private List<ExperimentDescriptor> loadExperiments(YAMLExperimentList experimentList) {
        final List<ExperimentDescriptor> experimentDescriptors = new ArrayList<>();

        for (YAMLExperimentInfo experimentInfo : experimentList.experiments) {
            try {
                final ExperimentDetail experimentDetail = YAMLParser.parseFile(
                        PathUtil.getExperimentPath(experimentInfo.experimentId).resolve("configuration.yaml"),
                        ExperimentDetail.class
                );

                //TODO: Perhaps incorporate user into experiment info (saved data structure != represented structure)
                experimentDescriptors.add(
                        new YAMLExperimentDescriptor(this, experimentInfo, experimentDetail, userDatabase)
                );
            } catch (IOException ignored) {
            }
        }

        return experimentDescriptors;
    }

    @Override
    public Optional<ExperimentDescriptor> getNextScheduledExperiment() {
        ExperimentDescriptor nextExperiment = null;

        for (ExperimentDescriptor descriptor : experimentDescriptors) {
            if (descriptor.getStatus() == ExperimentStatus.SCHEDULED) {
                if (nextExperiment == null)
                    nextExperiment = descriptor;
                else if (descriptor.getStart().isBefore(nextExperiment.getStart())) {
                    nextExperiment = descriptor;
                }
            }
        }

        return Optional.ofNullable(nextExperiment);
    }

    @Override
    public List<ExperimentDescriptor> getExperiments() {
        return experimentDescriptors;
    }

    @Override
    public synchronized ExperimentDescriptor addExperiment(ExperimentTemplate template, User owner) {
        final YAMLExperimentInfo experimentInfo = new YAMLExperimentInfo(
                template.name,
                owner.getId(),
                nextId++,
                ExperimentStatus.CREATED,
                template.start,
                template.end
        );

        final ExperimentDetail experimentDetail = new ExperimentDetail(template.nodes);

        final ExperimentDescriptor experiment = new YAMLExperimentDescriptor(this, experimentInfo, experimentDetail, userDatabase);

        experimentDescriptors.add(experiment);
        writeExperimentFile(experiment);
        return experiment;
    }

    @Override
    public synchronized void updateExperiment(ExperimentDescriptor experimentDescriptor) {
        for (int i = 0; i < experimentDescriptors.size(); i++) {
            if (experimentDescriptors.get(i).equals(experimentDescriptor)) {
                experimentDescriptors.set(i, experimentDescriptor);
                writeExperimentFile(experimentDescriptor);
                return;
            }
        }
    }

    @Override
    public List<ExperimentDescriptor> getExperimentsInTimeFrame(LocalDateTime start, LocalDateTime end) {
        final List<ExperimentDescriptor> matchingExperiments = new ArrayList<>();

        for (ExperimentDescriptor descriptor : experimentDescriptors) {
            if (descriptor.getStart().compareTo(start) >= 0 && descriptor.getStart().compareTo(end) <= 0 ||
                    descriptor.getEnd().compareTo(start) >= 0 && descriptor.getEnd().compareTo(end) <= 0)
                matchingExperiments.add(descriptor);
        }

        return matchingExperiments;
    }

    @Override
    public Optional<ExperimentDescriptor> getExperimentById(long id) {
        for (ExperimentDescriptor experiment : experimentDescriptors)
            if (experiment.getId() == id)
                return Optional.of(experiment);

        return Optional.empty();
    }

    public UserDatabase getUserDatabase() {
        return userDatabase;
    }

    private void writeExperimentFile(ExperimentDescriptor experimentDescriptor) {
        try {
            Files.createDirectories(PathUtil.getExperimentPath(experimentDescriptor.getId()));

            YAMLParser.writeFile(Paths.get(workingDirectory.toString(), "experiments.yaml"), YAMLExperimentList.fromExperimentDescriptorList(experimentDescriptors, nextId));
            YAMLParser.writeFile(
                    PathUtil.getExperimentPath(experimentDescriptor.getId()).resolve("configuration.yaml"),
                    new ExperimentDetail(experimentDescriptor.getNodes())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
