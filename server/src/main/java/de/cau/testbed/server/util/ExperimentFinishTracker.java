package de.cau.testbed.server.util;

import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.config.experiment.ExperimentNode;
import de.cau.testbed.server.constants.ExperimentStatus;
import de.cau.testbed.server.util.event.LogRetrievedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Flow;

/**
 * Determines when an experiment is finish by watching incoming {@link LogRetrievedEvent} events and setting the
 * experiment status to {@link ExperimentStatus#DONE} once all logs are retrieved.
 * Uses a timeout and sets the experiment status to {@link ExperimentStatus#FAILED_TO_RETRIEVE_LOGS} if not all logs
 * were retrieved within timeout period.
 */
public class ExperimentFinishTracker implements Flow.Subscriber<LogRetrievedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentFinishTracker.class);
    private static final int WAIT_TIMEOUT_MILLIS = 300_000;

    private Flow.Subscription subscription;

    private final ExperimentDescriptor descriptor;

    private final Set<String> retrievedIds;

    private Timer retrievalTimeoutTimer;


    public ExperimentFinishTracker(ExperimentDescriptor descriptor, List<String> retrievedIds) {
        this.descriptor = descriptor;
        this.retrievedIds = new HashSet<>(retrievedIds);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;

        checkRetrievedIds();

        if (!areAllLogsReceived()) {
            initializeTimer();
            subscription.request(1);
        }
    }

    public void initializeTimer() {
        retrievalTimeoutTimer = new Timer(true);

        // Wait until experiment end + timeout
        // If we initiated experiment stop, only wait for timeout
        final long timeout;
        synchronized (descriptor.getLockObject()) {
            if (descriptor.getStatus() == ExperimentStatus.STOPPING) {
                timeout = 0;
            }
            else {
                timeout = Math.max(0, ChronoUnit.MILLIS.between(LocalDateTime.now(), descriptor.getEnd()));
            }
        }

        retrievalTimeoutTimer.schedule(new TimeoutTimerTask(), timeout + WAIT_TIMEOUT_MILLIS);
    }

    private void cleanup() {
        subscription.cancel();

        if (retrievalTimeoutTimer != null)
            retrievalTimeoutTimer.cancel();
    }

    private void checkRetrievedIds() {
        if (!areAllLogsReceived())
            return;

        LOGGER.info(String.format(
                "[Experiment %d] Retrieved all logs. Experiment is done.",
                descriptor.getId()
        ));

        cleanup();

        synchronized (descriptor.getLockObject()) {
            descriptor.setStatus(ExperimentStatus.DONE);
        }
    }

    private boolean areAllLogsReceived() {
        for (ExperimentNode node : descriptor.getNodes())
            if (!retrievedIds.contains(node.id()))
                return false;

        return true;
    }

    @Override
    public void onNext(LogRetrievedEvent event) {
        if (!(event.experimentId() == descriptor.getId()))
            return;

        retrievedIds.add(event.nodeId());
        subscription.request(1);

        checkRetrievedIds();
    }

    @Override
    public void onError(Throwable throwable) {
    }

    @Override
    public void onComplete() {
    }

    class TimeoutTimerTask extends TimerTask {
        @Override
        public void run() {
            LOGGER.info(String.format(
                    "[Experiment %d] Failed to retrieve all logs.",
                    descriptor.getId()
            ));
            subscription.cancel();

            // Experiment might have been cancelled or stopped before
            synchronized (descriptor.getLockObject()) {
                if (!descriptor.getStatus().isFinished())
                    descriptor.setStatus(ExperimentStatus.FAILED_TO_RETRIEVE_LOGS);
            }
        }
    }
}
