package de.cau.testbed.server.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.config.experiment.ExperimentNode;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class ExperimentTemplate {
    public final String name;

    @NotNull
    public final LocalDateTime start;

    @NotNull
    public final LocalDateTime end;

    @NotNull
    public final List<ExperimentNode> nodes;

    @JsonCreator
    public ExperimentTemplate(
            @JsonProperty("name") String name,
            @JsonProperty("start") LocalDateTime start,
            @JsonProperty("end") LocalDateTime end,
            @JsonProperty("nodes") List<ExperimentNode> nodes
    ) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "ExperimentTemplate{" +
                "name='" + name + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", nodes=" + nodes +
                '}';
    }
}
