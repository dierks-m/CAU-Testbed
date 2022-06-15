package de.cau.testbed.server.module;

import de.cau.testbed.server.constants.KafkaTopic;
import de.cau.testbed.server.network.HeartbeatMessage;
import de.cau.testbed.server.network.KafkaNetworkReceiver;

public class HeartbeatThread extends Thread {
    private final KafkaNetworkReceiver<HeartbeatMessage> heartbeatReceiver;

    public HeartbeatThread() {
        this.heartbeatReceiver = new KafkaNetworkReceiver<>(HeartbeatMessage.getDeserializer(), KafkaTopic.HEARTBEAT, "testbed-server");
    }

    @Override
    public void run() {
        while (true) {
            final HeartbeatMessage heartbeat = heartbeatReceiver.receive();

            System.out.println("Received heartbeat: " + heartbeat);
        }
    }
}
