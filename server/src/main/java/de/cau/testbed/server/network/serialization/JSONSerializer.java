package de.cau.testbed.server.network.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;

public class JSONSerializer {
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public <T> byte[] serialize(T data) {
        try {
            return MAPPER.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException(String.format("Error serializing %s", data.toString()));
        }
    }
}
