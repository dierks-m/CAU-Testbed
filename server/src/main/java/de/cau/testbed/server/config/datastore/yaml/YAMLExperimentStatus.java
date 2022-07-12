package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class YAMLExperimentStatus {
    public final String owner;
    public final String experimentId;
    public final String name;
    public final boolean isScheduled;
    public final boolean isDone;
    public final boolean isStarted;

    public final LocalDateTime start;

    public final LocalDateTime end;

    public YAMLExperimentStatus(
            @JsonProperty("name") String name,
            @JsonProperty("owner") String owner,
            @JsonProperty("experimentId") String experimentId,
            @JsonProperty("isScheduled") boolean isScheduled,
            @JsonProperty("isDone") boolean isDone,
            @JsonProperty("isStarted") boolean isStarted,
            @JsonProperty("start") LocalDateTime start,
            @JsonProperty("end")LocalDateTime end
            ) {
        this.name = name;
        this.owner = owner;
        this.experimentId = experimentId;
        this.isScheduled = isScheduled;
        this.isDone = isDone;
        this.isStarted = isStarted;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "YAMLExperimentStatus{" +
                "owner='" + owner + '\'' +
                ", experimentId='" + experimentId + '\'' +
                ", isScheduled=" + isScheduled +
                ", isDone=" + isDone +
                '}';
    }
}
