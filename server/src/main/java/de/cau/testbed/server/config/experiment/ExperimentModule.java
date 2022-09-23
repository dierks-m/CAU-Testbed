package de.cau.testbed.server.config.experiment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.constants.DeviceType;

import java.util.Optional;

/**
 * Holds information about an individual module (e.g. ZOUL, SKY) for a given experiment node {@link ExperimentNode}.
 * This includes the used firmware and the utilized data collection methods (serial dump, serial forward and GPIO trace).
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ExperimentModule {
    @JsonProperty("id")
    public final DeviceType moduleType;

    @JsonProperty("firmware")
    public final String firmware;

    @JsonProperty("gpioTracer")
    public final boolean gpioTracer;

    @JsonProperty("serialDump")
    public final boolean serialDump;

    @JsonProperty("serialForward")
    public final boolean serialForward;

    @JsonCreator
    public ExperimentModule(
            @JsonProperty("id") DeviceType moduleType,
            @JsonProperty("firmware") String firmware,
            @JsonProperty("gpioTracer") Boolean gpioTracer,
            @JsonProperty("serialDump") Boolean serialDump,
            @JsonProperty("serialForward") Boolean serialForward
    ) {
        this.moduleType = moduleType;
        this.firmware = firmware;
        this.gpioTracer = Optional.ofNullable(gpioTracer).orElse(false);
        this.serialDump = Optional.ofNullable(serialDump).orElse(true);
        this.serialForward = Optional.ofNullable(serialForward).orElse(false);
    }

    @Override
    public String toString() {
        return "ExperimentModule{" +
                "moduleType=" + moduleType +
                ", firmware='" + firmware + '\'' +
                ", gpioTracer=" + gpioTracer +
                ", serialDump=" + serialDump +
                ", serialForward=" + serialForward +
                '}';
    }
}
