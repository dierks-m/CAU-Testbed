package de.cau.testbed.server.module;

import de.cau.testbed.server.config.datastore.Database;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.constants.ExperimentStatus;
import de.cau.testbed.server.constants.KafkaTopic;
import de.cau.testbed.server.network.KafkaNetworkSender;
import de.cau.testbed.server.network.NetworkSender;
import de.cau.testbed.server.network.message.ExperimentMessage;
import de.cau.testbed.server.network.serialization.ExperimentSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ExperimentSchedulingThread extends Thread {
    private final Object waitObject = new Object();
    private final Logger logger = LoggerFactory.getLogger(ExperimentSchedulingThread.class);
    private final Database database;
    private final NetworkSender<ExperimentMessage> experimentSender;

    public ExperimentSchedulingThread(Database database) {
        this.database = database;
        this.experimentSender = new KafkaNetworkSender<>(new ExperimentSerializer(), KafkaTopic.EXPERIMENT_PREPARATION);
    }

    @Override
    public void run() {
        while (true) {
            final Optional<ExperimentDescriptor> nextExperiment = database.getNextScheduledExperiment();

            if (nextExperiment.isEmpty()) {
                logger.info("No scheduled experiments.");
                suspendUntilWakeup();
            } else {
                final ExperimentDescriptor descriptor = nextExperiment.get();
                final long minuteDiff = ChronoUnit.MINUTES.between(LocalDateTime.now(), descriptor.getStart());

                if (minuteDiff <= 5) {
                    prepareExperiment(descriptor);
                } else {
                    trySleep(minuteDiff * 60 * 1000);
                }
            }
        }
    }

    private void prepareExperiment(ExperimentDescriptor descriptor) {
        logger.info("Preparing experiment " + descriptor.getName());

        experimentSender.send(null, new ExperimentMessage(descriptor));
        descriptor.setStatus(ExperimentStatus.STARTED);
        database.updateExperiment(descriptor);
    }

    public void wakeup() {
        synchronized (waitObject) {
            waitObject.notify();
        }
    }

    private void suspendUntilWakeup() {
        try {
            synchronized (waitObject) {
                waitObject.wait();
            }
        } catch (InterruptedException ignored) {
        }
    }

    private void trySleep(long millis) {
        try {
            synchronized (waitObject) {
                waitObject.wait(millis);
            }
        } catch (InterruptedException ignored) {
        }
    }
}
