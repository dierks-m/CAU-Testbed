package de.cau.testbed.server.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;

public class JSONDeserializer {
    public <T> T deserialize(byte[] data, Class<T> objectClass) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(new String(data, "UTF-8"), objectClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SerializationException(String.format("Failed to deserialize %s", new String(data)));
        }
    }
}
