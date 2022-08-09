package de.cau.testbed.server.config.experiment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.DeviceType;

import java.util.Optional;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ExperimentModule {
    @JsonProperty("id")
    public final DeviceType moduleType;

    @JsonProperty("firmwarePath")
    public final String firmwarePath;

    @JsonProperty("gpioTracer")
    public final boolean gpioTracer;

    @JsonProperty("serialDump")
    public final boolean serialDump;

    @JsonCreator
    public ExperimentModule(
            @JsonProperty("id") DeviceType moduleType,
            @JsonProperty("firmware") String firmwarePath,
            @JsonProperty("gpioTracer") Boolean gpioTracer,
            @JsonProperty("serialDump") Boolean serialDump
    ) {
        this.moduleType = moduleType;
        this.firmwarePath = firmwarePath;
        this.gpioTracer = Optional.ofNullable(gpioTracer).orElse(false);
        this.serialDump = Optional.ofNullable(serialDump).orElse(true);
    }

    @Override
    public String toString() {
        return "ExperimentModule{" +
                "moduleType=" + moduleType +
                ", firmwarePath='" + firmwarePath + '\'' +
                '}';
    }
}
