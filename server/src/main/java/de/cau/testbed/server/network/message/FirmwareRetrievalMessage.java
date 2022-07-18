package de.cau.testbed.server.network.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FirmwareRetrievalMessage {
    public final long experimentId;
    public final String firmwareName;
    public final String hostName;
    public final String userName;
    public final Path targetPath;

    public FirmwareRetrievalMessage(
            @JsonProperty("firmware") String firmwareName,
            @JsonProperty("hostName") String hostName,
            @JsonProperty("userName") String userName,
            @JsonProperty("targetPath") String targetPath,
            @JsonProperty("experimentId") String experimentId
    ) {
        this.firmwareName = firmwareName;
        this.hostName = hostName;
        this.userName = userName;
        this.targetPath = Paths.get(targetPath);
        this.experimentId = Long.parseLong(experimentId);
    }

    @Override
    public String toString() {
        return "FirmwareRetrievalMessage{" +
                "experimentId='" + experimentId + '\'' +
                ", firmwareName='" + firmwareName + '\'' +
                ", hostName='" + hostName + '\'' +
                ", userName='" + userName + '\'' +
                ", targetPath=" + targetPath +
                '}';
    }
}
