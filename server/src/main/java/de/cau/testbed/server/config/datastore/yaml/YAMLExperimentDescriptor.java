package de.cau.testbed.server.config.datastore.yaml;

import de.cau.testbed.server.config.datastore.User;
import de.cau.testbed.server.config.datastore.UserDatabase;
import de.cau.testbed.server.config.experiment.*;
import de.cau.testbed.server.constants.ExperimentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class YAMLExperimentDescriptor implements ExperimentDescriptor {
    private final long id;

    private final User owner;
    private final String name;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private ExperimentStatus status;
    private final List<ExperimentNode> nodes;

    public YAMLExperimentDescriptor(ExperimentInfo experimentInfo, ExperimentDetail experimentDetail, UserDatabase userTable) {
        final Optional<User> user = userTable.getUserById(experimentInfo.owner);
        if (user.isEmpty())
            throw new IllegalArgumentException("User with id " + experimentInfo.owner + " does not exist!");
        this.owner = user.get();

        this.id = experimentInfo.experimentId;
        this.name = experimentInfo.name;
        this.start = experimentInfo.start;
        this.end = experimentInfo.end;
        this.nodes = experimentDetail.nodes;
        this.status = experimentInfo.status;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public User getOwner() {
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
