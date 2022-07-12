package de.cau.testbed.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Path;

public class YAMLParser {
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper(new YAMLFactory());
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static <T> T parseFile(Path path, Class<T> objectClass) throws IOException {
        return MAPPER.readValue(path.toFile(), objectClass);
    }

    public static <T> void writeFile(Path path, T object) throws IOException {
        MAPPER.writeValue(path.toFile(), object);
    }
}
