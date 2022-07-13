package de.cau.testbed.server.network.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.config.experiment.ExperimentNode;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;

import java.time.LocalDateTime;
import java.util.List;

public class ExperimentMessage {
    @JsonProperty("name")
    public final String name;

    @JsonProperty("nodes")
    public final List<ExperimentNode> nodes;

    @JsonProperty("experimentId")
    public final String experimentId;

    @JsonProperty("start")
    public final LocalDateTime start;
    @JsonProperty("end")
    public final LocalDateTime end;

    public ExperimentMessage(ExperimentDescriptor experimentDescriptor) {
        this.name = experimentDescriptor.getName();
        this.nodes = experimentDescriptor.getNodes();
        this.experimentId = Long.toString(experimentDescriptor.getId());
        this.start = experimentDescriptor.getStart();
        this.end = experimentDescriptor.getEnd();
    }
}
