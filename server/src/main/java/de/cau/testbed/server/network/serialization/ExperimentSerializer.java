package de.cau.testbed.server.network.serialization;

import de.cau.testbed.server.config.Experiment;
import org.apache.kafka.common.serialization.Serializer;

public class ExperimentSerializer extends JSONSerializer implements Serializer<Experiment> {
    @Override
    public byte[] serialize(String topic, Experiment data) {
        return serialize(data);
    }
}
