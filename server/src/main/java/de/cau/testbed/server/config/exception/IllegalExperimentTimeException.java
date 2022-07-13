package de.cau.testbed.server.config.exception;

import jakarta.ws.rs.BadRequestException;

public class IllegalExperimentTimeException extends BadRequestException {
    public IllegalExperimentTimeException(String message) {
        super(message);
    }
}
