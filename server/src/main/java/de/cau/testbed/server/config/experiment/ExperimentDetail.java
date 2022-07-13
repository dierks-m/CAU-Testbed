package de.cau.testbed.server.config.experiment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ExperimentDetail {

    @JsonProperty("nodes")
    public final List<ExperimentNode> nodes;

    @JsonCreator
    public ExperimentDetail(
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
