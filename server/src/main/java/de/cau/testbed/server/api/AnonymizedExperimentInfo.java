package de.cau.testbed.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class AnonymizedExperimentInfo {
    @JsonProperty("name")
    public final String name;

    @JsonProperty("start")
    public final LocalDateTime start;

    @JsonProperty("end")
    public final LocalDateTime end;

    public AnonymizedExperimentInfo(String name, LocalDateTime start, LocalDateTime end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }
}
