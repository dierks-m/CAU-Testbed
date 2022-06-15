package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Experiment {
    public final String name;
    public final List<ExperimentNode> nodes;

    public Experiment(
            @JsonProperty("name") String name,
            @JsonProperty("nodes") List<ExperimentNode> nodes
    ) {
        this.name = name;
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "name='" + name + '\'' +
                ", nodes=" + nodes +
                '}';
    }
}
