package de.cau.testbed.server.api;

import de.cau.testbed.server.config.experiment.ExperimentNode;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.List;

public record QueuedExperimentTemplate(
        String name,
        @NotNull Duration duration,
        @NotNull List<ExperimentNode> nodes
) {
}
