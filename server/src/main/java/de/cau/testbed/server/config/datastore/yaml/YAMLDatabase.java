package de.cau.testbed.server.config.datastore.yaml;

import de.cau.testbed.server.api.ExperimentTemplate;
import de.cau.testbed.server.config.YAMLParser;
import de.cau.testbed.server.config.datastore.Database;
import de.cau.testbed.server.config.exception.TimeCollisionException;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.config.experiment.ExperimentDetail;
import de.cau.testbed.server.config.experiment.ExperimentInfo;
import de.cau.testbed.server.constants.ExperimentStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class YAMLDatabase implements Database {
    private final Path workingDirectory;

    private long nextId;
    private final List<ExperimentDescriptor> experimentDescriptors;

    public YAMLDatabase(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
        final YAMLExperimentList experimentList = loadExperimentList();
        this.nextId = experimentList.nextId;
        this.experimentDescriptors = loadExperiments(loadExperimentList());
    }

    private YAMLExperimentList loadExperimentList() {
        try {
            return YAMLParser.parseFile(Paths.get(workingDirectory.toString(), "experiments.yaml"), YAMLExperimentList.class);
        } catch (IOException e) {
            return new YAMLExperimentList(Collections.emptyList(), 1);
        }
    }

    private List<ExperimentDescriptor> loadExperiments(YAMLExperimentList experimentList) {
        final List<ExperimentDescriptor> experimentDescriptors = new ArrayList<>();

        for (ExperimentInfo experimentInfo : experimentList.experiments) {
            try {
                final ExperimentDetail experimentDetail = YAMLParser.parseFile(
                        Paths.get(workingDirectory.toString(), Long.toString(experimentInfo.experimentId), "configuration.yaml"),
                        ExperimentDetail.class
                );

                experimentDescriptors.add(
                        new YAMLExperimentDescriptor(experimentInfo, experimentDetail)
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
    public synchronized ExperimentDescriptor addExperiment(ExperimentTemplate template) {
        final ExperimentInfo experimentInfo = new ExperimentInfo(
                template.name,
                "dummy owner",
                nextId++,
                ExperimentStatus.CREATED,
                template.start,
                template.end
        );

        final ExperimentDetail experimentDetail = new ExperimentDetail(template.nodes);

        final ExperimentDescriptor experiment = new YAMLExperimentDescriptor(experimentInfo, experimentDetail);

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
    public synchronized long getNextExperimentId() {
        return nextId++;
    }

    private void writeExperimentFile(ExperimentDescriptor experimentDescriptor) {
        try {
            Files.createDirectories(Paths.get(workingDirectory.toString(), Long.toString(experimentDescriptor.getId())));

            YAMLParser.writeFile(Paths.get(workingDirectory.toString(), "experiments.yaml"), YAMLExperimentList.fromExperimentDescriptorList(experimentDescriptors, nextId));
            YAMLParser.writeFile(Paths.get(workingDirectory.toString(), Long.toString(experimentDescriptor.getId()), "configuration.yaml"), new ExperimentDetail(
                    experimentDescriptor.getNodes()
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
