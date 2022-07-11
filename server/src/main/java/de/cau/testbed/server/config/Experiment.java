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

    @JsonProperty("start")
    public final LocalDateTime start;
    @JsonProperty("end")
    public final LocalDateTime end;

    @JsonCreator
    public Experiment(
            @JsonProperty("nodes") List<ExperimentNode> nodes,
            @JsonProperty("start") LocalDateTime start,
            @JsonProperty("end") LocalDateTime end
    ) {
        this.nodes = nodes;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "nodes=" + nodes +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
