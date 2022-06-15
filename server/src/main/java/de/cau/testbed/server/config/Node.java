package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.DeviceStatus;
import de.cau.testbed.server.constants.DeviceType;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

public class Node {
    @NotNull
    private String id;
    private List<DeviceType> capabilities = Collections.emptyList();
    private DeviceStatus deviceStatus = DeviceStatus.WAIT_FOR_INITIAL_CONTACT;

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

    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", capabilities=" + capabilities +
                ", status=" + deviceStatus +
                '}';
    }
}
