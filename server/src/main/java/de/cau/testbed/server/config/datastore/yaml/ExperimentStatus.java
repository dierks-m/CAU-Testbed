package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExperimentStatus {
    public final String experimentId;
    public final boolean isScheduled;
    public final boolean isDone;

    public ExperimentStatus(
            @JsonProperty("experimentId") String experimentId,
            @JsonProperty("isScheduled") boolean isScheduled,
            @JsonProperty("isDone") boolean isDone
    ) {
        this.experimentId = experimentId;
        this.isScheduled = isScheduled;
        this.isDone = isDone;
    }

    @Override
    public String toString() {
        return "ExperimentStatus{" +
                "experimentId='" + experimentId + '\'' +
                ", isScheduled=" + isScheduled +
                ", isDone=" + isDone +
                '}';
    }
}
