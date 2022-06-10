package de.cau.testbed.shared.network;

public interface NetworkSender<T> {
    void send(Long key, T element);
}
