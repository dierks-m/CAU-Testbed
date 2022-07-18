package de.cau.testbed.server.config.exception;

import jakarta.ws.rs.BadRequestException;

public class FirmwareDoesNotExistException extends BadRequestException {
    public FirmwareDoesNotExistException(String message) {
        super(message);
    }
}
