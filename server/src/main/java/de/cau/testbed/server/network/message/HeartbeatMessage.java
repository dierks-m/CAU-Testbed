package de.cau.testbed.server.network.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.cau.testbed.server.network.serialization.HeartbeatDeserializer;
import de.cau.testbed.server.network.serialization.HeartbeatSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

@JsonDeserialize(as = Heartbeat.class)
@JsonSerialize(as = Heartbeat.class)
public interface HeartbeatMessage {
    @JsonProperty
    String getNodeId();

    static Serializer<Heartbeat> getSerializer() {
        return new HeartbeatSerializer();
    }

    static Deserializer<HeartbeatMessage> getDeserializer() {
        return new HeartbeatDeserializer();
    }
}

