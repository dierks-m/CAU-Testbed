package de.cau.testbed.server.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.UserType;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public class UserTemplate {
    @NotNull
    public final String name;
    public final UserType type;

    @JsonCreator
    public UserTemplate(
            @JsonProperty("name") String name,
            @JsonProperty("type") UserType type
    ) {
        this.name = name;
        this.type = Optional.ofNullable(type).orElse(UserType.USER);
    }
}
