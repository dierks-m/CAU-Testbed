package de.cau.testbed.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.cau.testbed.server.config.Experiment;
import de.cau.testbed.server.config.ExperimentModule;
import de.cau.testbed.server.config.ExperimentNode;
import de.cau.testbed.server.config.TestbedServerConfiguration;
import de.cau.testbed.server.config.datastore.yaml.ExperimentStatus;
import de.cau.testbed.server.config.datastore.yaml.YAMLDatabase;
import de.cau.testbed.server.config.datastore.yaml.YAMLExperimentDescriptor;
import de.cau.testbed.server.constants.DeviceType;
import de.cau.testbed.server.constants.KafkaTopic;
import de.cau.testbed.server.module.FirmwareDistributionThread;
import de.cau.testbed.server.module.HeartbeatThread;
import de.cau.testbed.server.module.LogRetrievalThread;
import de.cau.testbed.server.network.KafkaNetworkSender;
import de.cau.testbed.server.network.serialization.ExperimentSerializer;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class TestbedServerApplication extends Application<TestbedServerConfiguration> {
    public static void main(String[] args) throws Exception {
        new TestbedServerApplication().run(args);
    }

    @Override
    public void run(TestbedServerConfiguration configuration, Environment environment) throws Exception {
//        System.out.println(configuration.nodes);
//
//        Experiment experiment;
//
//        try {
//            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//            mapper.registerModule(new JavaTimeModule());
//            experiment = mapper.readValue(Paths.get("config/sample-experiment.yaml").toFile(), Experiment.class);
//
//            System.out.println(experiment);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        new HeartbeatThread().start();
//
//        new FirmwareDistributionThread(configuration.workingDirectory).start();
//        new LogRetrievalThread(configuration.workingDirectory).start();
//
//        KafkaNetworkSender<Experiment> experimentSender = new KafkaNetworkSender<>(new ExperimentSerializer(), KafkaTopic.EXPERIMENT_PREPARATION);

//        ObjectMapper mapper = new ObjectMapper();
//
//        ExperimentNode node = new ExperimentNode("abc", Arrays.asList(new ExperimentModule(DeviceType.NRF52, "abc")));
//
//        Experiment experiment1 = new Experiment("name", Arrays.asList(node), "idabc", LocalDateTime.now(), LocalDateTime.now());
//
//        System.out.println(mapper.writeValueAsString(experiment1));

//        experimentSender.send(null, experiment);

        final YAMLDatabase database = new YAMLDatabase(configuration.workingDirectory);

        System.out.println(database.getNextScheduledExperiment());
    }
}