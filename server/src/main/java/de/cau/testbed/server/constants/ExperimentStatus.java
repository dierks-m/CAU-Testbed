package de.cau.testbed.server.constants;

public enum ExperimentStatus {
    CREATED("Created", false),
    SCHEDULED("Scheduled", false),
    STARTED("Started", true, false),
    FAILED_TO_RETRIEVE_LOGS("Done, failed to retrieve all results", true, true),
    CANCELLED("Cancelled", true, true),
    DONE("Done", true, true),
    ;

    private final boolean isFinished;
    private final String displayValue;
    private final boolean hasStarted;

    ExperimentStatus(String displayValue, boolean hasStarted, boolean isFinished) {
        this.displayValue = displayValue;
        this.hasStarted = hasStarted;
        this.isFinished = isFinished;
    }

    ExperimentStatus(String displayValue, boolean isFinished) {
        this(displayValue, false, isFinished);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public String getDisplayValue() {
        return toString();
    }
}
