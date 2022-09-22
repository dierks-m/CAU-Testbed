package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestbedServerConfiguration extends Configuration {
    public final List<HardwareNode> nodes;
    public final Path workingDirectory;

    public final int numFirmwareDistributionThreads;

    public final int numLogRetrievalThreads;

    public final String kafkaAddress;
    public final int heartbeatInterval;

    public TestbedServerConfiguration(
            @JsonProperty("nodes") List<HardwareNode> nodes,
            @JsonProperty("workingDirectory") String workingDirectory,
            @JsonProperty("numFirmwareDistributionThreads") int numFirmwareDistributionThreads,
            @JsonProperty("numLogRetrievalThreads") int numLogRetrievalThreads,
            @JsonProperty("kafkaAddress") String kafkaAddress,
            @JsonProperty("heartbeatInterval") int heartbeatInterval
    ) {
        this.nodes = nodes;
        this.workingDirectory = Paths.get(workingDirectory);
        this.numFirmwareDistributionThreads = numFirmwareDistributionThreads;
        this.numLogRetrievalThreads = numLogRetrievalThreads;
        this.kafkaAddress = kafkaAddress;
        this.heartbeatInterval = heartbeatInterval;
    }
}