package de.cau.testbed.server.config.datastore;

import de.cau.testbed.server.api.ExperimentTemplate;
import de.cau.testbed.server.config.exception.TimeCollisionException;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * This provides an abstract way to interact with the database. Experiments can be loaded/added etc. this way.
 */
public interface Database {
    Optional<ExperimentDescriptor> getCurrentOrNextExperiment();

    Optional<ExperimentDescriptor> getFollowingExperiment(ExperimentDescriptor previous);
    Optional<ExperimentDescriptor> getNextScheduledExperiment();

    List<ExperimentDescriptor> getExperiments();

    /**
     * Adds a new experiment, but does not schedule it, yet.
     * @param experimentDescriptor
     */
    ExperimentDescriptor addExperiment(ExperimentTemplate experimentDescriptor, User owner) throws TimeCollisionException;

    void updateExperiment(ExperimentDescriptor experimentDescriptor);

    List<ExperimentDescriptor> getExperimentsInTimeFrame(LocalDateTime start, LocalDateTime end);

    Optional<ExperimentDescriptor> getExperimentById(long id);
}
