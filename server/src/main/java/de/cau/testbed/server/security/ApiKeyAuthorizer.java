package de.cau.testbed.server.security;

import de.cau.testbed.server.config.datastore.User;
import io.dropwizard.auth.Authorizer;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Authorizes whether a user can access the given resource with a provided role.
 * Role types are defined in {@link de.cau.testbed.server.constants.UserType} and returned by {@link User#getType()}
 */
public class ApiKeyAuthorizer implements Authorizer<User> {
    @Override
    public boolean authorize(User user, String role, @Nullable ContainerRequestContext requestContext) {
        return user.getType().getRoles().contains(role);
    }
}
