package de.cau.testbed.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.ExperimentStatus;

import java.time.LocalDateTime;

public class AnonymizedExperimentInfo {
    @JsonProperty("name")
    public final String name;

    @JsonProperty("start")
    public final LocalDateTime start;

    @JsonProperty("end")
    public final LocalDateTime end;

    @JsonProperty("status")
    public final String status;

    @JsonProperty("id")
    private final long id;

    public AnonymizedExperimentInfo(String name, LocalDateTime start, LocalDateTime end, long id, ExperimentStatus status) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.id = id;
        this.status = status.getDisplayValue();
    }
}
