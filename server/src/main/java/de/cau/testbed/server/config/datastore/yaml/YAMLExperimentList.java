package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.config.experiment.ExperimentInfo;

import java.util.ArrayList;
import java.util.List;

public class YAMLExperimentList {
    @JsonProperty
    public final List<ExperimentInfo> experiments;

    @JsonProperty
    public final long nextId;

    public YAMLExperimentList(
            @JsonProperty("experiments") List<ExperimentInfo> experiments,
            @JsonProperty("nextId") long nextId
    ) {
        this.experiments = experiments;
        this.nextId = nextId;
    }

    public static YAMLExperimentList fromExperimentDescriptorList(List<ExperimentDescriptor> experimentDescriptors, long nextId) {
        final List<ExperimentInfo> experimentStatusList = new ArrayList<>();

        for (ExperimentDescriptor descriptor : experimentDescriptors) {
            experimentStatusList.add(new ExperimentInfo(
                    descriptor.getName(),
                    descriptor.getOwner(),
                    descriptor.getId(),
                    descriptor.getStatus(),
                    descriptor.getStart(),
                    descriptor.getEnd())
            );
        }

        return new YAMLExperimentList(experimentStatusList, nextId);
    }

    @Override
    public String toString() {
        return "YAMLExperimentList{" +
                "experiments=" + experiments +
                '}';
    }
}
