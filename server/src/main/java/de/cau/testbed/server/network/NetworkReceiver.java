package de.cau.testbed.server.network;

/**
 * Provides a simple interface to receive network messages of a specific type.
 * This can e.g. be done via Apache Kafka.
 * @param <T>
 */
public interface NetworkReceiver<T> {
    T receive();
}
