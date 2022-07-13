package de.cau.testbed.server.config.experiment;

import de.cau.testbed.server.constants.ExperimentStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Describes an experiment with all its nodes/components.
 * This is a wrapper that provides abstraction from the database in the background.
 */
public interface ExperimentDescriptor {
    String getId();

    String getName();

    String getOwner();

    LocalDateTime getStart();

    LocalDateTime getEnd();

    ExperimentStatus getStatus();

    void setStatus(ExperimentStatus status);

    List<ExperimentNode> getNodes();
}
