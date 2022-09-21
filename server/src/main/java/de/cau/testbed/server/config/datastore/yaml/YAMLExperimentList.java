package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Data representation for the central 'experiments.yaml' file, which contains a list of
 * experiment information and the next experiment ID.
 */
public record YAMLExperimentList(@JsonProperty List<YAMLExperimentInfo> experiments, @JsonProperty long nextId) {
    public static YAMLExperimentList fromExperimentDescriptorList(List<ExperimentDescriptor> experimentDescriptors, long nextId) {
        final List<YAMLExperimentInfo> experimentStatusList = new ArrayList<>();

        for (ExperimentDescriptor descriptor : experimentDescriptors) {
            experimentStatusList.add(new YAMLExperimentInfo(
                    descriptor.getName(),
                    descriptor.getOwner().getId(),
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
