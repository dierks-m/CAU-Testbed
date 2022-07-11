package de.cau.testbed.server.network;

/**
 * Provides a simple interface to send network messages of a specific type.
 * This can e.g. be done via Apache Kafka.
 * @param <T>
 */
public interface NetworkSender<T> {
    /**
     * Send a message, key can be omitted.
     * @param key
     * May be omitted (null). Used for partitioning in Kafka.
     * @param element
     */
    void send(Long key, T element);
}
