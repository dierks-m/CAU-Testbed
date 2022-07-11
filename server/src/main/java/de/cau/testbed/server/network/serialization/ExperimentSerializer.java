package de.cau.testbed.server.network.serialization;

import de.cau.testbed.server.config.Experiment;
import de.cau.testbed.server.network.message.ExperimentMessage;
import org.apache.kafka.common.serialization.Serializer;

public class ExperimentSerializer extends JSONSerializer implements Serializer<ExperimentMessage> {
    @Override
    public byte[] serialize(String topic, ExperimentMessage data) {
        return serialize(data);
    }
}
