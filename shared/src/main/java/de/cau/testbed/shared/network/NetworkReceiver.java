package de.cau.testbed.shared.network;

public interface NetworkReceiver<T> {
    T receive();
}
