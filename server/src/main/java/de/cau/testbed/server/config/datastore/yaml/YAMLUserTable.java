package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.config.datastore.UserDatabase;
import de.cau.testbed.server.config.datastore.User;

import java.util.List;
import java.util.Optional;

public class YAMLUserTable implements UserDatabase {
    private final List<YAMLUser> users;
    private final long nextId;

    public YAMLUserTable(
            @JsonProperty("users") List<YAMLUser> users,
            @JsonProperty("nextId") long nextId

    ) {
        this.users = users;
        this.nextId = nextId;
    }

    public Optional<User> getUserById(long id) {
        for (YAMLUser user : users)
            if (user.getId() == id)
                return Optional.of(user);

        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByApiKey(String apiKey) {
        for (YAMLUser user : users)
            if (user.getApiKey().equals(apiKey))
                return Optional.of(user);

        return Optional.empty();
    }
}
