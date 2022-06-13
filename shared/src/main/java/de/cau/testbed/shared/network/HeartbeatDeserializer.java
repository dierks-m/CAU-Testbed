package de.cau.testbed.shared.network;

import org.apache.kafka.common.serialization.Deserializer;

public class HeartbeatDeserializer extends JSONDeserializer implements Deserializer<HeartbeatMessage> {
    @Override
    public HeartbeatMessage deserialize(String topic, byte[] data) {
        return deserialize(data, HeartbeatMessage.class);
    }
}
