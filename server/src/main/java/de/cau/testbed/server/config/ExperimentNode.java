package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.DeviceType;

import java.util.List;

public class ExperimentNode {
    public final String id;
    public final List<ExperimentModule> modules;

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
