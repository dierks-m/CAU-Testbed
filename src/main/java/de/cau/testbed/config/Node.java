package de.cau.testbed.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

public class Node {
    @NotNull
    private String id;
    private List<DeviceType> capabilities = Collections.emptyList();

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty
    public List<DeviceType> getCapabilities() {
        return capabilities;
    }

    @JsonProperty
    public void setCapabilities(List<DeviceType> capabilities) {
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
