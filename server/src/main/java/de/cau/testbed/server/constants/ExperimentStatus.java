package de.cau.testbed.server.constants;

public enum ExperimentStatus {
    CREATED(false),
    SCHEDULED(false),
    STARTED(true, false),
    FAILED_TO_RETRIEVE_LOGS(true, true),
    CANCELLED(true, true),
    DONE(true, true),
    ;

    private final boolean isFinished;
    private final boolean hasStarted;

    ExperimentStatus(boolean hasStarted, boolean isFinished) {
        this.hasStarted = hasStarted;
        this.isFinished = isFinished;
    }

    ExperimentStatus(boolean isFinished) {
        this(false, isFinished);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean hasStarted() {
        return hasStarted;
    }
}
