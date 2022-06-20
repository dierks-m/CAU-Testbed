package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class Experiment {
    public final String name;
    public final List<ExperimentNode> nodes;
    public final String experimentId;

    public final LocalDateTime start;
    public final LocalDateTime end;

    public Experiment(
            @JsonProperty("name") String name,
            @JsonProperty("nodes") List<ExperimentNode> nodes,
            @JsonProperty("experimentId") String experimentId,
            @JsonProperty("start") LocalDateTime start,
            @JsonProperty("end") LocalDateTime end
    ) {
        this.name = name;
        this.nodes = nodes;
        this.experimentId = experimentId;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "name='" + name + '\'' +
                ", nodes=" + nodes +
                ", experimentId='" + experimentId + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
