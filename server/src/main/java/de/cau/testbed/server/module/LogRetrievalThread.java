package de.cau.testbed.server.module;

import de.cau.testbed.server.constants.KafkaConstants;
import de.cau.testbed.server.constants.KafkaTopic;
import de.cau.testbed.server.network.KafkaNetworkReceiver;
import de.cau.testbed.server.network.fileTransfer.FileTransferHandler;
import de.cau.testbed.server.network.fileTransfer.NodeTransferTarget;
import de.cau.testbed.server.network.fileTransfer.SCPFileTransferHandler;
import de.cau.testbed.server.network.message.LogRetrievalMessage;
import de.cau.testbed.server.network.serialization.LogRetrievalMessageDeserializer;
import de.cau.testbed.server.util.PathUtil;
import de.cau.testbed.server.util.event.LogRetrievedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.SubmissionPublisher;

public class LogRetrievalThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(LogRetrievalThread.class);

    private final KafkaNetworkReceiver<LogRetrievalMessage> logRetrievalReceiver;

    private final FileTransferHandler fileTransferHandler;
    private final SubmissionPublisher<LogRetrievedEvent> logEventHandler;
    private final int id;

    public LogRetrievalThread(Path workingDirectory, SubmissionPublisher<LogRetrievedEvent> logEventHandler, int id) {
        logger.info("Initializing thread " + id);
        this.logEventHandler = logEventHandler;
        this.id = id;
        this.logRetrievalReceiver = new KafkaNetworkReceiver<>(
                new LogRetrievalMessageDeserializer(),
                KafkaTopic.LOG_RETRIEVAL,
                KafkaConstants.CLIENT_ID
        );
        this.fileTransferHandler = new SCPFileTransferHandler(workingDirectory);
    }

    @Override
    public void run() {
        while (true) {
            final LogRetrievalMessage retrievalMessage = logRetrievalReceiver.receive();

            try {
                final Path logPath = getValidExperimentLogPath(retrievalMessage);

                if (!Files.isDirectory(logPath))
                    Files.createDirectories(logPath);

                logRetrievalIntent(retrievalMessage);

                fileTransferHandler.download(
                        new NodeTransferTarget(
                                retrievalMessage.host,
                                retrievalMessage.userName,
                                retrievalMessage.path
                        ),
                        logPath
                );

                logSuccessfulRetrieval(retrievalMessage);

                logEventHandler.submit(new LogRetrievedEvent(retrievalMessage.experimentId, retrievalMessage.nodeId));
            } catch (Exception e) {
                logger.error(String.format(
                        "[Experiment %d] Failed to execute log transfer for node %s due to ",
                        retrievalMessage.experimentId,
                        retrievalMessage.nodeId,
                        e
                ));
            }
        }
    }

    private Path getValidExperimentLogPath(LogRetrievalMessage retrievalMessage) {
        return getValidExperimentLogPath(retrievalMessage.experimentId, retrievalMessage.nodeId);
    }

    private Path getValidExperimentLogPath(long experimentId, String nodeId) {
        return PathUtil.getLogPath(experimentId).resolve(nodeId);
    }

    private void logSuccessfulRetrieval(LogRetrievalMessage retrievalMessage) {
        logger.info(String.format(
                "[Thread %d] [Experiment %d] Transferred logs for node %s",
                id,
                retrievalMessage.experimentId,
                retrievalMessage.nodeId
        ));
    }

    private void logRetrievalIntent(LogRetrievalMessage retrievalMessage) {
        logger.info(String.format(
                "[Thread %d] [Experiment %d] Node %s requests transfer of logs",
                id,
                retrievalMessage.experimentId,
                retrievalMessage.nodeId
        ));
    }
}
