package de.cau.testbed.network;

public interface NetworkSender<T> {
    void send(Long key, T element);
}
