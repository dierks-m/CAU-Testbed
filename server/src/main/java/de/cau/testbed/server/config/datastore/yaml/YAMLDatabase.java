package de.cau.testbed.server.config.datastore.yaml;

import de.cau.testbed.server.config.Experiment;
import de.cau.testbed.server.config.YAMLParser;
import de.cau.testbed.server.config.datastore.Database;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.constants.ExperimentStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class YAMLDatabase implements Database {
    private final Path workingDirectory;
    private final List<ExperimentDescriptor> experimentDescriptors;

    public YAMLDatabase(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
        this.experimentDescriptors = loadExperiments(loadExperimentList());
    }

    private YAMLExperimentList loadExperimentList() {
        try {
            return YAMLParser.parseFile(Paths.get(workingDirectory.toString(), "experiments.yaml"), YAMLExperimentList.class);
        } catch (IOException e) {
            return new YAMLExperimentList(Collections.emptyList());
        }
    }

    private List<ExperimentDescriptor> loadExperiments(YAMLExperimentList experimentList) {
        final List<ExperimentDescriptor> experimentDescriptors = new ArrayList<>();

        for (YAMLExperimentStatus experimentStatus : experimentList.experiments) {
            try {
                final Experiment experiment = YAMLParser.parseFile(
                        Paths.get(workingDirectory.toString(), experimentStatus.experimentId, "configuration.yaml"),
                        Experiment.class
                );

                experimentDescriptors.add(
                        new YAMLExperimentDescriptor(experimentStatus, experiment)
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
    public synchronized void addExperiment(ExperimentDescriptor experimentDescriptor) {
        experimentDescriptors.add(experimentDescriptor);

        writeExperimentFile(experimentDescriptor);
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

        addExperiment(experimentDescriptor);
    }

    private void writeExperimentFile(ExperimentDescriptor experimentDescriptor) {
        try {
            Files.createDirectories(Paths.get(workingDirectory.toString(), experimentDescriptor.getId()));

            YAMLParser.writeFile(Paths.get(workingDirectory.toString(), "experiments.yaml"), YAMLExperimentList.fromExperimentDescriptorList(experimentDescriptors));
            YAMLParser.writeFile(Paths.get(workingDirectory.toString(), experimentDescriptor.getId(), "configuration.yaml"), new Experiment(
                    experimentDescriptor.getNodes()
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
