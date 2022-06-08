package de.cau.testbedNode.util;

import de.cau.testbed.constants.KafkaTopic;
import de.cau.testbed.network.HeartbeatMessage;
import de.cau.testbed.network.KafkaNetworkSender;
import de.cau.testbedNode.network.Heartbeat;

public class HeartbeatThread extends Thread {
    private final KafkaNetworkSender<HeartbeatMessage> heartbeatSender;
    private final String nodeId;

    public HeartbeatThread(String nodeId) {
        this.heartbeatSender = new KafkaNetworkSender<>(HeartbeatMessage.getSerializer(), KafkaTopic.HEARTBEAT);
        this.nodeId = nodeId;
    }

    @Override
    public void run() {
        while (true) {
            heartbeatSender.send(null, new Heartbeat(nodeId));

            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
