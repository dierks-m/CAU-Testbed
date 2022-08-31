package de.cau.testbed.server.module;

import de.cau.testbed.server.config.datastore.Database;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.constants.ExperimentStatus;
import de.cau.testbed.server.constants.KafkaTopic;
import de.cau.testbed.server.constants.NodeInvocationMethod;
import de.cau.testbed.server.network.KafkaNetworkSender;
import de.cau.testbed.server.network.NetworkSender;
import de.cau.testbed.server.network.message.ExperimentMessage;
import de.cau.testbed.server.network.serialization.ExperimentSerializer;
import de.cau.testbed.server.util.ExperimentFinishTrackerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ExperimentSchedulingThread extends Thread {
    private static final int PREPARE_BUFFER_SEC = 120;
    private final Object waitObject = new Object();
    private final Logger logger = LoggerFactory.getLogger(ExperimentSchedulingThread.class);
    private final Database database;
    private final ExperimentFinishTrackerFactory trackerFactory;
    private final NetworkSender<ExperimentMessage> experimentSender;

    public ExperimentSchedulingThread(Database database, ExperimentFinishTrackerFactory trackerFactory) {
        this.database = database;
        this.trackerFactory = trackerFactory;
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
                final long secondDiff = ChronoUnit.SECONDS.between(LocalDateTime.now(), descriptor.getStart());

                if (secondDiff <= PREPARE_BUFFER_SEC) {
                    prepareExperiment(descriptor);
                } else {
                    logger.info("Next experiment is " + secondDiff + " seconds away. Sleeping.");
                    trySleep((secondDiff - PREPARE_BUFFER_SEC) * 1000);
                }
            }
        }
    }

    private void prepareExperiment(ExperimentDescriptor descriptor) {
        if (descriptor.getEnd().isBefore(LocalDateTime.now())) {
            logger.info(String.format(
                    "[Experiment %d] %s ended before current time. Skipping and setting status to DONE.",
                    descriptor.getId(),
                    descriptor.getName()
            ));
            descriptor.setStatus(ExperimentStatus.DONE);

            return;
        }

        logger.info(String.format(
                "[Experiment %d] Preparing experiment %s",
                descriptor.getId(),
                descriptor.getName()
        ));

        experimentSender.send(null, new ExperimentMessage(descriptor, NodeInvocationMethod.START));
        descriptor.setStatus(ExperimentStatus.STARTED);
        trackerFactory.createExperimentFinishTracker(descriptor);
    }

    public void cancelExperiment(ExperimentDescriptor experiment) {
        if (experiment.getStatus().isFinished())
            return;

        experiment.setStatus(ExperimentStatus.CANCELLED);
        experimentSender.send(null, new ExperimentMessage(experiment, NodeInvocationMethod.CANCEL));
    }

    public void stopExperiment(ExperimentDescriptor experiment) {
        if (experiment.getStatus().isFinished())
            return;

        experimentSender.send(null, new ExperimentMessage(experiment, NodeInvocationMethod.STOP));
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
