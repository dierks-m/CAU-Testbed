package de.cau.testbed.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class TimeFrame {
    public final LocalDateTime start;
    public final LocalDateTime end;

    public TimeFrame(
            @JsonProperty("start") LocalDateTime start,
            @JsonProperty("end") LocalDateTime end
    ) {
        this.start = start;
        this.end = end;
    }
}
