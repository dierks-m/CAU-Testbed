package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YAMLExperimentStatus {
    public final String owner;
    public final String experimentId;
    public final boolean isScheduled;
    public final boolean isDone;

    public YAMLExperimentStatus(
            @JsonProperty("owner") String owner,
            @JsonProperty("experimentId") String experimentId,
            @JsonProperty("isScheduled") boolean isScheduled,
            @JsonProperty("isDone") boolean isDone
    ) {
        this.owner = owner;
        this.experimentId = experimentId;
        this.isScheduled = isScheduled;
        this.isDone = isDone;
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
