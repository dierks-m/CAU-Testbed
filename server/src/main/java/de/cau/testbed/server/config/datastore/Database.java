package de.cau.testbed.server.config.datastore;

import de.cau.testbed.server.config.experiment.ExperimentDescriptor;

import java.util.Optional;

public interface Database {
    Optional<ExperimentDescriptor> getNextScheduledExperiment();
}
