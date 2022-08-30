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

public class ExperimentFinishTracker implements Flow.Subscriber<LogRetrievedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentFinishTracker.class);
    private static final int WAIT_TIMEOUT_MSEC = 300_000;

    private Flow.Subscription subscription;

    private final ExperimentDescriptor descriptor;

    private final Set<String> retrievedIds;


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
        final Timer retrievalFailedTimer = new Timer(true);

        // Wait until experiment end + timeout
        final long timeout = Math.min(
                Math.max(0, ChronoUnit.MILLIS.between(LocalDateTime.now(), descriptor.getEnd()) + WAIT_TIMEOUT_MSEC),
                WAIT_TIMEOUT_MSEC
        );

        retrievalFailedTimer.schedule(new TimeoutTimerTask(), timeout);
    }

    private void checkRetrievedIds() {
        if (!areAllLogsReceived())
            return;

        LOGGER.info(String.format(
                "[Experiment %d] Retrieved all logs. Experiment is done.",
                descriptor.getId()
        ));

        subscription.cancel();
        descriptor.setStatus(ExperimentStatus.DONE);
    }

    private boolean areAllLogsReceived() {
        for (ExperimentNode node : descriptor.getNodes())
            if (!retrievedIds.contains(node.id))
                return false;

        return true;
    }

    @Override
    public void onNext(LogRetrievedEvent event) {
        if (!(event.experimentId() == descriptor.getId()))
            return;

        retrievedIds.add(event.nodeId());
        checkRetrievedIds();

        subscription.request(1);
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
            descriptor.setStatus(ExperimentStatus.FAILED_TO_RETRIEVE_LOGS);
        }
    }
}
