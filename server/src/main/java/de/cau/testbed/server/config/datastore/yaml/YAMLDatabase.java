package de.cau.testbed.server.config.datastore.yaml;

import de.cau.testbed.server.config.Experiment;
import de.cau.testbed.server.config.YAMLParser;
import de.cau.testbed.server.config.datastore.Database;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;

import java.io.IOException;
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

        for (ExperimentStatus experimentStatus : experimentList.experiments) {
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
            if (descriptor.isScheduled() && !descriptor.isDone()) {
                if (nextExperiment == null)
                    nextExperiment = descriptor;
                else if (descriptor.getStart().isBefore(nextExperiment.getStart())) {
                    nextExperiment = descriptor;
                }
            }
        }

        return Optional.ofNullable(nextExperiment);
    }
}
