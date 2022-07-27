package de.cau.testbed.server;

import de.cau.testbed.server.config.TestbedServerConfiguration;
import de.cau.testbed.server.config.datastore.yaml.YAMLDatabase;
import de.cau.testbed.server.config.datastore.User;
import de.cau.testbed.server.module.ExperimentSchedulingThread;
import de.cau.testbed.server.module.FirmwareDistributionThread;
import de.cau.testbed.server.module.HeartbeatThread;
import de.cau.testbed.server.module.LogRetrievalThread;
import de.cau.testbed.server.resources.ExperimentResource;
import de.cau.testbed.server.resources.UploadFirmwareResource;
import de.cau.testbed.server.security.ApiKeyAuthenticator;
import de.cau.testbed.server.security.ApiKeyAuthorizer;
import de.cau.testbed.server.service.ExperimentService;
import de.cau.testbed.server.service.FirmwareService;
import de.cau.testbed.server.util.PathUtil;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

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
        PathUtil.initialize(configuration.workingDirectory);

        new HeartbeatThread().start();

        new FirmwareDistributionThread(configuration.workingDirectory).start();
        new LogRetrievalThread(configuration.workingDirectory).start();

        final YAMLDatabase database = new YAMLDatabase(configuration.workingDirectory);

        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new ApiKeyAuthenticator(database.getUserDatabase()))
                .setAuthorizer(new ApiKeyAuthorizer())
                .setRealm("API-KEY-AUTH-REALM")
                .buildAuthFilter()
        ));
        environment.jersey().register(new RolesAllowedDynamicFeature());
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

        final ExperimentSchedulingThread schedulingThread = new ExperimentSchedulingThread(database);
        schedulingThread.start();

        final ExperimentService experimentService = new ExperimentService(database, configuration.nodes, schedulingThread, configuration.workingDirectory);
        environment.jersey().register(new ExperimentResource(experimentService));

        final FirmwareService firmwareService = new FirmwareService(database);
        environment.jersey().register(new UploadFirmwareResource(firmwareService));
    }
}