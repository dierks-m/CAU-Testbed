package de.cau.testbed.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.DeviceType;

public class ExperimentModule {
    public final DeviceType moduleType;
    public final String firmwarePath;

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
