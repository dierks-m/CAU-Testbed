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

    @JsonProperty("firmware")
    public final String firmwarePath;

    @JsonProperty("gpioTracer")
    public final boolean gpioTracer;

    @JsonProperty("serialDump")
    public final boolean serialDump;

    @JsonProperty("serialForward")
    public final boolean serialForward;

    @JsonCreator
    public ExperimentModule(
            @JsonProperty("id") DeviceType moduleType,
            @JsonProperty("firmware") String firmwarePath,
            @JsonProperty("gpioTracer") Boolean gpioTracer,
            @JsonProperty("serialDump") Boolean serialDump,
            @JsonProperty("serialForward") Boolean serialForward
    ) {
        this.moduleType = moduleType;
        this.firmwarePath = firmwarePath;
        this.gpioTracer = Optional.ofNullable(gpioTracer).orElse(false);
        this.serialDump = Optional.ofNullable(serialDump).orElse(true);
        this.serialForward = Optional.ofNullable(serialForward).orElse(false);
    }

    @Override
    public String toString() {
        return "ExperimentModule{" +
                "moduleType=" + moduleType +
                ", firmwarePath='" + firmwarePath + '\'' +
                ", gpioTracer=" + gpioTracer +
                ", serialDump=" + serialDump +
                ", serialForward=" + serialForward +
                '}';
    }
}
