package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.DeviceType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class HardwareNode {
    @NotNull
    public final String id;
    public final List<DeviceType> capabilities;

    public HardwareNode(
            @JsonProperty("id") String id,
            @JsonProperty("capabilities") List<DeviceType> capabilities
    ) {
        this.id = id;
        this.capabilities = capabilities;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", capabilities=" + capabilities +
                '}';
    }
}
