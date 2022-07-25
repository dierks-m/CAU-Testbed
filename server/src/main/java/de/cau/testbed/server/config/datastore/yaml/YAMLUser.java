package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.config.datastore.User;
import de.cau.testbed.server.constants.UserType;

public class YAMLUser implements User {
    private final String name;
    private final long id;
    private final String apiKey;
    private final UserType type;

    public YAMLUser(
            @JsonProperty("name") String name,
            @JsonProperty("id") long id,
            @JsonProperty("apiKey") String apiKey,
            @JsonProperty("type") UserType type
    ) {
        this.name = name;
        this.id = id;
        this.apiKey = apiKey;
        this.type = type;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UserType getType() {
        return type;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }
}
