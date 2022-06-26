package de.cau.testbed.server.network.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LogRetrievalMessage {
    public final String host;
    public final String userName;
    public final String experimentId;
    public final String nodeId;
    public final Path path;

    public LogRetrievalMessage(
            @JsonProperty("hostName") String host,
            @JsonProperty("userName") String userName,
            @JsonProperty("experimentId") String experimentId,
            @JsonProperty("nodeId") String nodeId,
            @JsonProperty("targetPath") String path
    ) {
        this.host = host;
        this.userName = userName;
        this.experimentId = experimentId;
        this.nodeId = nodeId;
        this.path = Paths.get(path);
    }
}
