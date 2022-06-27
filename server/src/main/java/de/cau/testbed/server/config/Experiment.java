package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Experiment {
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

    @JsonCreator
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
