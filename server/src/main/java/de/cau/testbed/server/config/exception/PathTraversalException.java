package de.cau.testbed.server.config.exception;

import java.io.IOException;

public class PathTraversalException extends IOException {
    public PathTraversalException(String message) {
        super(message);
    }
}
