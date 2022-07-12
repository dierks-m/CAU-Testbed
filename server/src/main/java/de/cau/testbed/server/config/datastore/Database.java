package de.cau.testbed.server.config.datastore;

import de.cau.testbed.server.config.exception.TimeCollisionException;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;

import java.util.Optional;

/**
 * This provides an abstract way to interact with the database. Experiments can be loaded/added etc. this way.
 */
public interface Database {
    Optional<ExperimentDescriptor> getNextScheduledExperiment();

    /**
     * Adds a new experiment, but does not schedule it, yet.
     * @param experimentDescriptor
     */
    void addExperiment(ExperimentDescriptor experimentDescriptor) throws TimeCollisionException;

    void updateExperiment(ExperimentDescriptor experimentDescriptor);
}
