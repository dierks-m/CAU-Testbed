package de.cau.testbed.server.util.event;

public interface EventObserver<T> {
    void onEvent(T event);
}
