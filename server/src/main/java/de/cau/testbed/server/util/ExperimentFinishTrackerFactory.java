package de.cau.testbed.server.util;

import de.cau.testbed.server.config.datastore.Database;
import de.cau.testbed.server.config.experiment.ExperimentDescriptor;
import de.cau.testbed.server.util.event.LogRetrievedEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.SubmissionPublisher;

public class ExperimentFinishTrackerFactory {
    private final SubmissionPublisher<LogRetrievedEvent> logRetrievalPublisher;

    public ExperimentFinishTrackerFactory(SubmissionPublisher<LogRetrievedEvent> logRetrievalPublisher) {
        this.logRetrievalPublisher = logRetrievalPublisher;
    }

    public ExperimentFinishTracker createExperimentFinishTracker(ExperimentDescriptor descriptor) {
        final ExperimentFinishTracker tracker = new ExperimentFinishTracker(descriptor, Collections.emptyList());
        logRetrievalPublisher.subscribe(tracker);

        return tracker;
    }

    public List<ExperimentFinishTracker> createInitialTrackers(Database database) {
        final List<ExperimentDescriptor> experiments = database.getExperiments();
        final List<ExperimentFinishTracker> trackers = new ArrayList<>();

        for (ExperimentDescriptor descriptor : experiments) {
            if (descriptor.getStatus().hasStarted() && !descriptor.getStatus().isFinished()) {
                final List<String> retrievedLogs = compileRetrievedLogList(descriptor.getId());

                final ExperimentFinishTracker tracker = new ExperimentFinishTracker(descriptor, retrievedLogs);
                logRetrievalPublisher.subscribe(tracker);

                trackers.add(tracker);
            }
        }

        return trackers;
    }

    private List<String> compileRetrievedLogList(long experimentId) {
        final File logPath = PathUtil.getLogPath(experimentId).toFile();

        if (!logPath.isDirectory())
            return Collections.emptyList();

        return Arrays.asList(logPath.list());
    }
}