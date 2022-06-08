package de.cau.testbedNode.network;

import de.cau.testbed.network.HeartbeatMessage;

public class Heartbeat implements HeartbeatMessage {
    private final String nodeId;

    public Heartbeat(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }
}
