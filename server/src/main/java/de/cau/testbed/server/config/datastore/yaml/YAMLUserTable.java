package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.config.datastore.User;
import de.cau.testbed.server.constants.UserType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class YAMLUserTable {
    @JsonProperty("users")
    private final List<YAMLUser> users;

    @JsonProperty("nextId")
    private long nextId;

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

    public Optional<User> getUserByApiKey(String apiKey) {
        for (YAMLUser user : users)
            if (user.getApiKey().equals(apiKey))
                return Optional.of(user);

        return Optional.empty();
    }

    public User addUser(String name, UserType type) {
        final YAMLUser user = new YAMLUser(
                name,
                nextId++,
                UUID.randomUUID().toString(),
                type
        );

        users.add(user);

        return user;
    }
}
