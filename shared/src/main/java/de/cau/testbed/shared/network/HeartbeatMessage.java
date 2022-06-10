package de.cau.testbed.shared.network;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

public interface HeartbeatMessage {
    @JsonProperty
    String getNodeId();

    static Serializer<HeartbeatMessage> getSerializer() {
        return new HeartbeatSerializer();
    }

    static Deserializer<HeartbeatMessage> getDeserializer() {
        return new HeartbeatDeserializer();
    }
}

class HeartbeatSerializer extends JSONSerializer implements Serializer<HeartbeatMessage> {
    @Override
    public byte[] serialize(String topic, HeartbeatMessage data) {
        return serialize(data);
    }
}

class HeartbeatDeserializer extends JSONDeserializer implements Deserializer<HeartbeatMessage> {
    @Override
    public HeartbeatMessage deserialize(String topic, byte[] data) {
        return deserialize(data, HeartbeatMessage.class);
    }
}