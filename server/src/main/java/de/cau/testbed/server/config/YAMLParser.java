package de.cau.testbed.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Path;

public class YAMLParser {
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper(new YAMLFactory());
        MAPPER.registerModule(new JavaTimeModule());
    }

    public static <T> T parseFile(Path path, Class<T> objectClass) throws IOException {
        return MAPPER.readValue(path.toFile(), objectClass);
    }
}
