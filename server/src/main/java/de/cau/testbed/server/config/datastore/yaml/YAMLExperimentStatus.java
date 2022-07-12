package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.ExperimentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class YAMLExperimentStatus {
    public final String owner;
    public final String experimentId;
    public final String name;
    public final ExperimentStatus status;
    public final LocalDateTime start;

    public final LocalDateTime end;

    public YAMLExperimentStatus(
            @JsonProperty("name") String name,
            @JsonProperty("owner") String owner,
            @JsonProperty("experimentId") String experimentId,
            @JsonProperty("status") ExperimentStatus status,
            @JsonProperty("start") LocalDateTime start,
            @JsonProperty("end")LocalDateTime end
            ) {
        this.name = name;
        this.owner = owner;
        this.experimentId = experimentId;
        this.status = status;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "YAMLExperimentStatus{" +
                "owner='" + owner + '\'' +
                ", experimentId='" + experimentId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
