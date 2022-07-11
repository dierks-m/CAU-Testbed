package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;

import java.util.ArrayList;
import java.util.List;

public class YAMLExperimentList {
    public final List<YAMLExperimentStatus> experiments;

    public YAMLExperimentList(
            @JsonProperty("experiments") List<YAMLExperimentStatus> experiments
    ) {
        this.experiments = experiments;
    }

    public static YAMLExperimentList fromExperimentDescriptorList(List<ExperimentDescriptor> experimentDescriptors) {
        final List<YAMLExperimentStatus> experimentStatusList = new ArrayList<>();

        for (ExperimentDescriptor descriptor : experimentDescriptors) {
            experimentStatusList.add(new YAMLExperimentStatus(descriptor.getOwner(), descriptor.getId(), descriptor.isScheduled(), descriptor.isDone()));
        }

        return new YAMLExperimentList(experimentStatusList);
    }

    @Override
    public String toString() {
        return "YAMLExperimentList{" +
                "experiments=" + experiments +
                '}';
    }
}