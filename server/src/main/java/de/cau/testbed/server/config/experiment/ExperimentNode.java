package de.cau.testbed.server.config.experiment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.config.experiment.ExperimentModule;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ExperimentNode that = (ExperimentNode) other;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
