package de.cau.testbed.server.config.datastore.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class YAMLExperimentList {
    public final List<ExperimentStatus> experiments;

    public YAMLExperimentList(
            @JsonProperty("experiments") List<ExperimentStatus> experiments
    ) {
        this.experiments = experiments;
    }

    @Override
    public String toString() {
        return "YAMLExperimentList{" +
                "experiments=" + experiments +
                '}';
    }
}
