package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.DeviceType;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ExperimentNode {
    @JsonProperty("id")
    public final String id;

    @JsonProperty("modules")
    public final List<ExperimentModule> modules;

    @JsonCreator
    public ExperimentNode(
            @JsonProperty("id") String id,
            @JsonProperty("modules") List<ExperimentModule> modules
    ) {
        this.id = id;
        this.modules = modules;
    }

    @Override
    public String toString() {
        return "ExperimentNode{" +
                "id='" + id + '\'' +
                ", modules=" + modules +
                '}';
    }
}
