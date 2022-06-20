package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Experiment {
    public final String name;
    public final List<ExperimentNode> nodes;
    public final String experimentId;

    public Experiment(
            @JsonProperty("name") String name,
            @JsonProperty("nodes") List<ExperimentNode> nodes,
            @JsonProperty("experimentId") String experimentId
    ) {
        this.name = name;
        this.nodes = nodes;
        this.experimentId = experimentId;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "name='" + name + '\'' +
                ", nodes=" + nodes +
                ", experimentId='" + experimentId + '\'' +
                '}';
    }
}
