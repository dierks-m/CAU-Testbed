package de.cau.testbed.server.network.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Heartbeat implements HeartbeatMessage {
    private final String nodeId;

    @JsonCreator
    public Heartbeat(@JsonProperty("id") String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return "Heartbeat{" +
                "nodeId='" + nodeId + '\'' +
                '}';
    }
}
