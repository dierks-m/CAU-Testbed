package de.cau.testbed.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;

public class JSONSerializer {
    public <T> byte[] serialize(T data) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException(String.format("Error serializing %s", data.toString()));
        }
    }
}
