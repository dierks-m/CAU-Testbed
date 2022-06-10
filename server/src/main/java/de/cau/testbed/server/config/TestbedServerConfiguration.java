package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

import java.util.Collections;
import java.util.List;

public class TestbedServerConfiguration extends Configuration {
    private List<Node> nodes = Collections.emptyList();

    @JsonProperty
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    @JsonProperty
    public List<Node> getNodes() {
        return nodes;
    }
}