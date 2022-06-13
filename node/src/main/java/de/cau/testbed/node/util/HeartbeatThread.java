package de.cau.testbed.node.util;

import de.cau.testbed.shared.constants.KafkaConstants;
import de.cau.testbed.shared.constants.KafkaTopic;
import de.cau.testbed.shared.network.Heartbeat;
import de.cau.testbed.shared.network.HeartbeatSerializer;
import de.cau.testbed.shared.network.KafkaNetworkSender;

public class HeartbeatThread extends Thread {
    private final KafkaNetworkSender<Heartbeat> heartbeatSender;
    private final String nodeId;

    public HeartbeatThread(String nodeId) {
        this.heartbeatSender = new KafkaNetworkSender<>(new HeartbeatSerializer(), KafkaTopic.HEARTBEAT);
        this.nodeId = nodeId;
    }

    @Override
    public void run() {
        while (true) {
            heartbeatSender.send(null, new Heartbeat(nodeId));

            try {
                Thread.sleep(KafkaConstants.HEARTBEAT_INTERVAL - 1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
