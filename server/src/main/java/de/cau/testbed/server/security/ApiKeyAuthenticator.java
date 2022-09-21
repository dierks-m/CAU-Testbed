package de.cau.testbed.server.security;

import de.cau.testbed.server.config.datastore.UserDatabase;
import de.cau.testbed.server.config.datastore.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;

/**
 * Authenticates users using an API key that is provided in the user field of the HTTP header.
 * The password field is ignored.
 */
public class ApiKeyAuthenticator implements Authenticator<BasicCredentials, User> {
    private final UserDatabase userDatabase;

    public ApiKeyAuthenticator(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        return userDatabase.getUserByApiKey(credentials.getUsername());
    }
}
