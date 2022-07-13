package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestbedServerConfiguration extends Configuration {
    public final List<HardwareNode> nodes;
    public final Path workingDirectory;
    public TestbedServerConfiguration(
            @JsonProperty("nodes") List<HardwareNode> nodes,
            @JsonProperty("workingDirectory") String workingDirectory
    ) {
        this.nodes = nodes;
        this.workingDirectory = Paths.get(workingDirectory);
    }
}