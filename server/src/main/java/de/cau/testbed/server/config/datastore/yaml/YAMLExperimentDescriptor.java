package de.cau.testbed.server.config.datastore.yaml;

import de.cau.testbed.server.config.experiment.ExperimentNode;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.constants.ExperimentStatus;

import java.time.LocalDateTime;
import java.util.List;

public class YAMLExperimentDescriptor implements ExperimentDescriptor {
    private final String id;

    private final String owner;
    private final String name;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private ExperimentStatus status;
    private final List<ExperimentNode> nodes;

    public YAMLExperimentDescriptor(YAMLExperimentInfo experimentInfo, YAMLExperimentDetail experimentDetail) {
        this.owner = experimentInfo.owner;
        this.id = experimentInfo.experimentId;
        this.name = experimentInfo.name;
        this.start = experimentInfo.start;
        this.end = experimentInfo.end;
        this.nodes = experimentDetail.nodes;
        this.status = experimentInfo.status;
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
    public ExperimentStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ExperimentStatus status) {
        this.status = status;
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
                ", nodes=" + nodes +
                '}';
    }
}
