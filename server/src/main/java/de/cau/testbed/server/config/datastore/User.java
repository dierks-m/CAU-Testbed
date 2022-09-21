package de.cau.testbed.server.config.datastore;

import de.cau.testbed.server.constants.UserType;

import java.security.Principal;

/**
 * User object used both for authentication and identification of ownership for experiments.
 */
public interface User extends Principal {
    long getId();

    String getName();

    UserType getType();

    String getApiKey();
}
