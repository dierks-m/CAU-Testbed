package de.cau.testbed.server.network;

public interface NetworkSender<T> {
    void send(Long key, T element);
}
