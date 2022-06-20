package de.cau.testbed.server.network.serialization;

import de.cau.testbed.server.network.FirmwareRetrievalMessage;
import org.apache.kafka.common.serialization.Deserializer;

public class FirmwareRetrievalMessageDeserializer extends JSONDeserializer implements Deserializer<FirmwareRetrievalMessage> {
    @Override
    public FirmwareRetrievalMessage deserialize(String topic, byte[] data) {
        return deserialize(data, FirmwareRetrievalMessage.class);
    }
}
