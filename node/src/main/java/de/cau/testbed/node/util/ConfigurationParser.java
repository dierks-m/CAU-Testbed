package de.cau.testbed.node.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigurationParser<T> {
    private final Class<T> configClass;

    public ConfigurationParser(Class<T> configClass) {
        this.configClass = configClass;
    }

    public T parseFile(Path path) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        return objectMapper.readValue(path.toFile(), configClass);
    }
}
