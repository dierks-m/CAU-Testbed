package de.cau.testbed.server.config.exception;

import jakarta.ws.rs.BadRequestException;

public class NoSuchExperimentException extends BadRequestException {
    public NoSuchExperimentException(String message) {
        super(message);
    }
}
