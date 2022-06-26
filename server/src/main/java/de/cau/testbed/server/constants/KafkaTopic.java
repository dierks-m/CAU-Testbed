package de.cau.testbed.server.constants;

public enum KafkaTopic {
    HEARTBEAT("heartbeat"),
    EXPERIMENT_PREPARATION("experimentPreparation"),
    FIRMWARE_RETRIEVAL("firmwareRetrieval"),
    LOG_RETRIEVAL("logRetrieval"),
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
