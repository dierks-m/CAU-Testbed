package de.cau.testbed.server.config.exception;

import jakarta.ws.rs.BadRequestException;

public class TimeCollisionException extends BadRequestException {
    public TimeCollisionException(String message) {
        super(message);
    }
}
