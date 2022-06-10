package de.cau.testbed.server;

import de.cau.testbed.server.config.TestbedServerConfiguration;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;

public class TestbedServerApplication extends Application<TestbedServerConfiguration> {
    public static void main(String[] args) throws Exception {
        new TestbedServerApplication().run(args);
    }

    @Override
    public void run(TestbedServerConfiguration configuration, Environment environment) throws Exception {
        System.out.println(configuration.getNodes());
    }
}
