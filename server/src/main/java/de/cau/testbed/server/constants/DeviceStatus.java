package de.cau.testbed.server.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DeviceStatus {
    WAIT_FOR_INITIAL_CONTACT("Waiting for initial contact"),
    ALIVE("Alive"),
    RECONNECT("Reconnected after timeout", true),
    DEAD("Dead", true),
    ;

    private final String value;
    private final boolean wasDead;

    DeviceStatus(String value, boolean wasDead) {
        this.value = value;
        this.wasDead = wasDead;
    }

    DeviceStatus(String value) {
        this(value, false);
    }

    public boolean wasDead() {
        return wasDead;
    }

    @JsonValue
    public String toValue() {
        return value;
    }
}
