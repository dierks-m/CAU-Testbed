package de.cau.testbed.server.config.datastore.yaml;

import de.cau.testbed.server.config.Experiment;
import de.cau.testbed.server.config.ExperimentNode;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;

import java.time.LocalDateTime;
import java.util.List;

public class YAMLExperimentDescriptor implements ExperimentDescriptor {
    private final String id;

    private final String owner;
    private final String name;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final boolean scheduled;
    private final boolean done;
    private boolean isStarted;
    private final List<ExperimentNode> nodes;

    public YAMLExperimentDescriptor(YAMLExperimentStatus experimentStatus, Experiment experiment) {
        this.owner = experimentStatus.owner;
        this.id = experimentStatus.experimentId;
        this.name = experimentStatus.name;
        this.start = experiment.start;
        this.end = experiment.end;
        this.scheduled = experimentStatus.isScheduled;
        this.done = experimentStatus.isDone;
        this.nodes = experiment.nodes;
        this.isStarted = experimentStatus.isStarted;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public LocalDateTime getStart() {
        return start;
    }

    @Override
    public LocalDateTime getEnd() {
        return end;
    }

    @Override
    public boolean isScheduled() {
        return scheduled;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public void setStarted(boolean started) {
        isStarted = started;
    }

    @Override
    public List<ExperimentNode> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return "YAMLExperimentDescriptor{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", scheduled=" + scheduled +
                ", done=" + done +
                ", nodes=" + nodes +
                '}';
    }
}
