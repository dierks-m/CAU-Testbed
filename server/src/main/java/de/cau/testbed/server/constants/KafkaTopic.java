package de.cau.testbed.server.constants;

public enum KafkaTopic {
    HEARTBEAT("heartbeat"),
    ;

    private final String name;

    KafkaTopic(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
