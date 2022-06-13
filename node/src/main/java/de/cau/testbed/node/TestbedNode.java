package de.cau.testbed.node;

import de.cau.testbed.node.config.NodeConfiguration;
import de.cau.testbed.node.util.ConfigurationParser;
import de.cau.testbed.node.util.HeartbeatThread;

import java.io.IOException;
import java.nio.file.Paths;

public class TestbedNode {
    private final HeartbeatThread heartbeatThread;

    public TestbedNode(String nodeId) {
        this.heartbeatThread = new HeartbeatThread(nodeId);
        this.heartbeatThread.start();
    }

    public static void main(String[] args) throws IOException {
        final ConfigurationParser<NodeConfiguration> configurationParser = new ConfigurationParser<>(NodeConfiguration.class);
        final NodeConfiguration configuration = configurationParser.parseFile(Paths.get(args[0]));

        new TestbedNode(configuration.getId());
        System.in.read();
    }
}
