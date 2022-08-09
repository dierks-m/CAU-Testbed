package de.cau.testbed.server.constants;

import java.util.Collections;
import java.util.List;

public enum UserType {
    USER("USER"),
    ADMIN("ADMIN"),
    ;

    private final List<String> roles;

    UserType(String role) {
        roles = Collections.singletonList(role);
    }

    public List<String> getRoles() {
        return roles;
    }
}
