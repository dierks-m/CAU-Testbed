package de.cau.testbed.server.config.datastore;

import java.util.Optional;

public interface UserDatabase {
    Optional<User> getUserByApiKey(String apiKey);
}
