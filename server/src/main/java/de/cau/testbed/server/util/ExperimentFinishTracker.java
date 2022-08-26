package de.cau.testbed.server.util;

import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.config.experiment.ExperimentNode;
import de.cau.testbed.server.constants.ExperimentStatus;
import de.cau.testbed.server.util.event.LogRetrievedEvent;

import java.util.*;
import java.util.concurrent.Flow;

public class ExperimentFinishTracker implements Flow.Subscriber<LogRetrievedEvent> {
    private static final int WAIT_TIMEOUT_MSEC = 300_000;

    private Flow.Subscription subscription;

    private final Timer retrievalFailedTimer;

    private final ExperimentDescriptor descriptor;

    private final Set<String> retrievedIds;


    public ExperimentFinishTracker(ExperimentDescriptor descriptor, List<String> retrievedIds) {
        this.descriptor = descriptor;
        this.retrievedIds = new HashSet<>(retrievedIds);

        this.retrievalFailedTimer = new Timer(true);
        retrievalFailedTimer.schedule(new TimeoutTimerTask(), WAIT_TIMEOUT_MSEC);
    }

    private void checkRetrievedIds() {
        for (ExperimentNode node : descriptor.getNodes())
            if (!retrievedIds.contains(node.id))
                return;

        subscription.cancel();
        descriptor.setStatus(ExperimentStatus.DONE);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
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
    public void onError(Throwable throwable) {}

    @Override
    public void onComplete() {}

    class TimeoutTimerTask extends TimerTask {
        @Override
        public void run() {
            subscription.cancel();
            descriptor.setStatus(ExperimentStatus.FAILED_TO_RETRIEVE_LOGS);
        }
    }
}
