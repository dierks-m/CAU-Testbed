package de.cau.testbed.shared.network;

import org.apache.kafka.common.serialization.Serializer;

public class HeartbeatSerializer extends JSONSerializer implements Serializer<Heartbeat> {
    @Override
    public byte[] serialize(String topic, Heartbeat data) {
        return serialize(data);
    }
}
