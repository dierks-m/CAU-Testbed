package de.cau.testbed.server.config.exception;

import jakarta.ws.rs.BadRequestException;

public class UnknownNodeException extends BadRequestException {
    public UnknownNodeException(String message) {
        super(message);
    }
}
