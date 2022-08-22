package de.cau.testbed.server.service;

import de.cau.testbed.server.module.NodeStatusObject;

import java.util.List;

public class NodeService {
    private final List<NodeStatusObject> nodeStatusList;

    public NodeService(List<NodeStatusObject> nodeStatusList) {
        this.nodeStatusList = nodeStatusList;
    }
    public List<NodeStatusObject> getNodeStatus() {
        return nodeStatusList;
    }
}
