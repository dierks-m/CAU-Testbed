package de.cau.testbed.server.module;

import de.cau.testbed.shared.constants.KafkaTopic;
import de.cau.testbed.shared.network.HeartbeatMessage;
import de.cau.testbed.shared.network.KafkaNetworkReceiver;

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
