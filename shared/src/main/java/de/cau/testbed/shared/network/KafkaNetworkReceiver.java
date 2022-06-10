package de.cau.testbed.shared.network;

import de.cau.testbed.shared.constants.KafkaConstants;
import de.cau.testbed.shared.constants.KafkaTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.LongDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaNetworkReceiver<T> implements NetworkReceiver<T> {
    private static final Duration consumerTimeout = Duration.ofMillis(KafkaConstants.CONSUMER_TIMEOUT);

    private final KafkaConsumer<Long, T> consumer;

    public KafkaNetworkReceiver(Deserializer<T> deserializer, KafkaTopic receiveTopic, String consumerID) {
        final Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_ADDRESS);
        consumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, KafkaConstants.CLIENT_ID);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getClass().getName());
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerID);

        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(receiveTopic.toString()));
    }

    @Override
    public T receive() {
        while (true) {
            final ConsumerRecords<Long, T> records = consumer.poll(consumerTimeout);

            if (!records.isEmpty()) {
                return records.iterator().next().value();
            }
        }
    }
}
