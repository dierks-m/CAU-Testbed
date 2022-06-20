package de.cau.testbed.server.module;

import de.cau.testbed.server.constants.KafkaTopic;
import de.cau.testbed.server.network.message.HeartbeatMessage;
import de.cau.testbed.server.network.KafkaNetworkReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(HeartbeatThread.class);
    private final KafkaNetworkReceiver<HeartbeatMessage> heartbeatReceiver;

    public HeartbeatThread() {
        this.heartbeatReceiver = new KafkaNetworkReceiver<>(HeartbeatMessage.getDeserializer(), KafkaTopic.HEARTBEAT, "testbed-server");
    }

    @Override
    public void run() {
        while (true) {
            final HeartbeatMessage heartbeat = heartbeatReceiver.receive();
            logger.trace("Received heartbeat from node " + heartbeat.getNodeId());
        }
    }
}
