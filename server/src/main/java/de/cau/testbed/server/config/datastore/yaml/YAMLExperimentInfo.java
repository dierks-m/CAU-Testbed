package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.ExperimentStatus;

import java.time.LocalDateTime;

/**
 * Data representation for an individual entry in the 'experiments.yaml' file that contains information about
 * start, stop, name, owner and experiment ID, which is used for linking to the individual
 * configuration file.
 */
record YAMLExperimentInfo(
        @JsonProperty("name") String name,
        @JsonProperty("owner") long owner,
        @JsonProperty("experimentId") long experimentId,
        @JsonProperty("status") ExperimentStatus status,
        @JsonProperty("start") LocalDateTime start,
        @JsonProperty("end") LocalDateTime end
) {
    @Override
    public String toString() {
        return "YAMLExperimentInfo1{" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                ", experimentId=" + experimentId +
                ", status=" + status +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}