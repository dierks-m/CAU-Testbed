package de.cau.testbed.server.network;

public interface NetworkReceiver<T> {
    T receive();
}
