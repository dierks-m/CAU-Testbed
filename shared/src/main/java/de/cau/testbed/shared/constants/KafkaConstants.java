package de.cau.testbed.shared.constants;

public interface KafkaConstants {
    String KAFKA_ADDRESS = "localhost:9092";
    String CLIENT_ID = "testbed_server";
    int CONSUMER_TIMEOUT = 1_000;

    int HEARTBEAT_INTERVAL = 10_000; // in ms
}
