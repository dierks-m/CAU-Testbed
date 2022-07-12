package de.cau.testbed.server.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExperimentId {
    public final String id;

    @JsonCreator
    public ExperimentId(
            @JsonProperty("id") String id
    ) {
        this.id = id;
    }
}
