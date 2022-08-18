package de.cau.testbed.server.util.event;

import java.util.*;

public class EventHandler<T> {
    private final List<EventObserver<T>> observers;

    private final Queue<T> events;

    private final EventHandlingThread<T> eventHandlingThread;

    public EventHandler() {
        observers = new ArrayList<>();
        events = new LinkedList<>();
        eventHandlingThread = new EventHandlingThread<>(observers, events);
        eventHandlingThread.start();
    }

    public void addEventObserver(EventObserver<T> observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void publishEvent(T event) {
        synchronized (events) {
            events.add(event);
        }

        eventHandlingThread.update();
    }

    public void cleanup() {
        eventHandlingThread.interrupt();
    }
}

class EventHandlingThread<T> extends Thread {
    private final List<EventObserver<T>> observers;
    private final Queue<T> events;

    private final Object lock = new Object();

    public EventHandlingThread(List<EventObserver<T>> observers, Queue<T> events) {
        this.observers = observers;
        this.events = events;
    }

    public void run() {
        while (true) {
            if (!processEvent())
                tryWait();
        }
    }

    private boolean processEvent() {
        final T event;

        synchronized (events) {
            event = events.poll();
        }

        if (event == null)
            return false;

        synchronized (observers) {
            for (EventObserver<T> observer : observers) {
                observer.onEvent(event);
            }
        }

        return true;
    }

    private void tryWait() {
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void update() {
        synchronized (lock) {
            lock.notify();
        }
    }
}
