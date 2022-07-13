package de.cau.testbed.server.config.experiment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.DeviceType;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ExperimentModule {
    @JsonProperty("id")
    public final DeviceType moduleType;

    @JsonProperty("firmwarePath")
    public final String firmwarePath;

    @JsonCreator
    public ExperimentModule(
            @JsonProperty("id") DeviceType moduleType,
            @JsonProperty("firmware") String firmwarePath
    ) {
        this.moduleType = moduleType;
        this.firmwarePath = firmwarePath;
    }

    @Override
    public String toString() {
        return "ExperimentModule{" +
                "moduleType=" + moduleType +
                ", firmwarePath='" + firmwarePath + '\'' +
                '}';
    }
}
