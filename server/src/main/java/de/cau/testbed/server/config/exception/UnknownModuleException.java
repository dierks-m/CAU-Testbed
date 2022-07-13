package de.cau.testbed.server.config.exception;

import jakarta.ws.rs.BadRequestException;

public class UnknownModuleException extends BadRequestException {
    public UnknownModuleException(String message) {
        super(message);
    }
}
