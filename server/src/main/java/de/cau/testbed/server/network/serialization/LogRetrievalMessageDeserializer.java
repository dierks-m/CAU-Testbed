package de.cau.testbed.server.network.serialization;

import de.cau.testbed.server.network.message.LogRetrievalMessage;
import org.apache.kafka.common.serialization.Deserializer;

public class LogRetrievalMessageDeserializer extends JSONDeserializer implements Deserializer<LogRetrievalMessage> {
    @Override
    public LogRetrievalMessage deserialize(String topic, byte[] data) {
        return deserialize(data, LogRetrievalMessage.class);
    }
}
