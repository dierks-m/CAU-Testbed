package de.cau.testbed.server.network;

import de.cau.testbed.server.constants.KafkaConstants;
import de.cau.testbed.server.constants.KafkaTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Properties;

public class KafkaNetworkSender<T> implements NetworkSender<T> {
    private static String kafkaAddress;
    private final KafkaTopic sendTopic;
    private final KafkaProducer<Long, T> producer;

    public KafkaNetworkSender(Serializer<T> serializer, KafkaTopic sendTopic) {
        this.sendTopic = sendTopic;

        final Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAddress);
        producerProps.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaConstants.CLIENT_ID);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer.getClass().getName());
        producer = new KafkaProducer<>(producerProps);
    }

    @Override
    public void send(Long key, T element) {
        producer.send(new ProducerRecord<>(sendTopic.toString(), key, element));
    }

    public static void setKafkaAddress(String address) {
        kafkaAddress = address;
    }
}
