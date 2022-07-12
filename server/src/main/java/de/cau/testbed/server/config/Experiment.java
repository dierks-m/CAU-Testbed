package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Experiment {

    @JsonProperty("nodes")
    public final List<ExperimentNode> nodes;

    @JsonCreator
    public Experiment(
            @JsonProperty("nodes") List<ExperimentNode> nodes
    ) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "nodes=" + nodes +
                '}';
    }
}
