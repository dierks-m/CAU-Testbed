package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

import java.util.Collections;
import java.util.List;

public class TestbedServerConfiguration extends Configuration {
    public final List<Node> nodes;

    public TestbedServerConfiguration(
            @JsonProperty("nodes") List<Node> nodes
    ) {
        this.nodes = nodes;
    }
}