package de.cau.testbed.server;

import de.cau.testbed.server.config.TestbedServerConfiguration;
import de.cau.testbed.server.config.datastore.yaml.YAMLDatabase;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;

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