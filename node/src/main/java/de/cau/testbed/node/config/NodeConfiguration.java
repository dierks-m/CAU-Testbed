package de.cau.testbed.node.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeConfiguration {
    private String id;

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public void setId(String id) {
        this.id = id;
    }
}
