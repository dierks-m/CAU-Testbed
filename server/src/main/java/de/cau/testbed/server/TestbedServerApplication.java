package de.cau.testbed.server;

import de.cau.testbed.server.config.TestbedServerConfiguration;
import de.cau.testbed.server.config.datastore.yaml.YAMLDatabase;
import de.cau.testbed.server.module.ExperimentSchedulingThread;
import de.cau.testbed.server.module.FirmwareDistributionThread;
import de.cau.testbed.server.module.HeartbeatThread;
import de.cau.testbed.server.module.LogRetrievalThread;
import de.cau.testbed.server.resources.CreateExperimentResource;
import de.cau.testbed.server.resources.ScheduleExperimentResource;
import de.cau.testbed.server.resources.UploadFirmwareResource;
import de.cau.testbed.server.service.ExperimentService;
import de.cau.testbed.server.service.FirmwareService;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;

public class TestbedServerApplication extends Application<TestbedServerConfiguration> {
    public static void main(String[] args) throws Exception {
        new TestbedServerApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<TestbedServerConfiguration> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addBundle(new MultiPartBundle());
    }

    @Override
    public void run(TestbedServerConfiguration configuration, Environment environment) {
        new HeartbeatThread().start();

        new FirmwareDistributionThread(configuration.workingDirectory).start();
        new LogRetrievalThread(configuration.workingDirectory).start();

        final YAMLDatabase database = new YAMLDatabase(configuration.workingDirectory);
        final ExperimentSchedulingThread schedulingThread = new ExperimentSchedulingThread(database);
        schedulingThread.start();

        final ExperimentService experimentService = new ExperimentService(database, configuration.nodes, schedulingThread, configuration.workingDirectory);
        environment.jersey().register(new CreateExperimentResource(experimentService));
        environment.jersey().register(new ScheduleExperimentResource(experimentService));

        final FirmwareService firmwareService = new FirmwareService(configuration.workingDirectory);
        environment.jersey().register(new UploadFirmwareResource(firmwareService));
    }
}