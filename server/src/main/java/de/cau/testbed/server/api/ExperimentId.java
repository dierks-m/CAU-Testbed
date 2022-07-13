package de.cau.testbed.server.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class ExperimentId {
    @NotNull
    public final long id;

    @JsonCreator
    public ExperimentId(
            @JsonProperty("id") long id
    ) {
        this.id = id;
    }
}
