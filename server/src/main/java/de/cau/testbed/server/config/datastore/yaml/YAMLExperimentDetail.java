package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.config.ExperimentNode;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class YAMLExperimentDetail {

    @JsonProperty("nodes")
    public final List<ExperimentNode> nodes;

    @JsonCreator
    public YAMLExperimentDetail(
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
