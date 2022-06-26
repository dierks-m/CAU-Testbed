package de.cau.testbed.server.network.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogRetrievalMessage {
    public final String host;
    public final String userName;
    public final String experimentId;
    public final String nodeId;
    public final String path;

    public LogRetrievalMessage(
            @JsonProperty("host") String host,
            @JsonProperty("userName") String userName,
            @JsonProperty("experimentId") String experimentId,
            @JsonProperty("nodeId") String nodeId,
            @JsonProperty("path") String path
    ) {
        this.host = host;
        this.userName = userName;
        this.experimentId = experimentId;
        this.nodeId = nodeId;
        this.path = path;
    }
}
