package de.cau.testbed.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorMessage {
    @JsonProperty("error")
    private final String message;

    public ErrorMessage(String message) {
        this.message = message;
    }
}
