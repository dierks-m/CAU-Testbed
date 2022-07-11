package de.cau.testbed.server.config.experiment;

import de.cau.testbed.server.config.ExperimentNode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Describes an experiment with all its nodes/components.
 * This is a wrapper that provides abstraction from the database in the background
 */
public interface ExperimentDescriptor {
    String getId();
    String getName();

    String getOwner();
    LocalDateTime getStart();
    LocalDateTime getEnd();
    boolean isScheduled();
    boolean isDone();

    List<ExperimentNode> getNodes();
}
