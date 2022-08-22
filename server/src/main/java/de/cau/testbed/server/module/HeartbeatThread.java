package de.cau.testbed.server.module;

import de.cau.testbed.server.constants.KafkaTopic;
import de.cau.testbed.server.network.message.HeartbeatMessage;
import de.cau.testbed.server.network.KafkaNetworkReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class HeartbeatThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(HeartbeatThread.class);
    private final KafkaNetworkReceiver<HeartbeatMessage> heartbeatReceiver;

    private final List<NodeStatusObject> nodeStatusList;


    public HeartbeatThread(List<String> nodes) {
        this.nodeStatusList = nodes.stream().map(x -> new NodeStatusObject(x)).collect(Collectors.toList());
        this.heartbeatReceiver = new KafkaNetworkReceiver<>(HeartbeatMessage.getDeserializer(), KafkaTopic.HEARTBEAT, "testbed-server");
    }

    @Override
    public void run() {
        while (true) {
            final HeartbeatMessage heartbeat = heartbeatReceiver.receive();

            nodeStatusList.forEach(x -> x.onHeartbeat(heartbeat.getNodeId()));

            logger.debug("Received heartbeat from node " + heartbeat.getNodeId());
        }
    }

    public List<NodeStatusObject> getNodeStatusList() {
        return nodeStatusList;
    }
}
