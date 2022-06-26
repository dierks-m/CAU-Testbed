package de.cau.testbed.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.cau.testbed.server.config.Experiment;
import de.cau.testbed.server.config.TestbedServerConfiguration;
import de.cau.testbed.server.module.FirmwareDistributionThread;
import de.cau.testbed.server.module.HeartbeatThread;
import de.cau.testbed.server.module.LogRetrievalThread;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestbedServerApplication extends Application<TestbedServerConfiguration> {
    public static void main(String[] args) throws Exception {
        new TestbedServerApplication().run(args);
    }

    @Override
    public void run(TestbedServerConfiguration configuration, Environment environment) throws Exception {
        System.out.println(configuration.nodes);

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.registerModule(new JavaTimeModule());
            Experiment experiment = mapper.readValue(Paths.get("config/sample-experiment.yaml").toFile(), Experiment.class);

            System.out.println(experiment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        new HeartbeatThread().start();

        new FirmwareDistributionThread(configuration.workingDirectory).start();
        new LogRetrievalThread(configuration.workingDirectory).start();
    }
}