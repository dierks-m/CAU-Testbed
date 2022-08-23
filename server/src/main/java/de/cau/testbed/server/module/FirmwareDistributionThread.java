package de.cau.testbed.server.module;

import de.cau.testbed.server.util.PathUtil;
import de.cau.testbed.server.constants.KafkaConstants;
import de.cau.testbed.server.constants.KafkaTopic;
import de.cau.testbed.server.network.fileTransfer.NodeTransferTarget;
import de.cau.testbed.server.network.message.FirmwareRetrievalMessage;
import de.cau.testbed.server.network.KafkaNetworkReceiver;
import de.cau.testbed.server.network.fileTransfer.FileTransferHandler;
import de.cau.testbed.server.network.fileTransfer.SCPFileTransferHandler;
import de.cau.testbed.server.network.serialization.FirmwareRetrievalMessageDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FirmwareDistributionThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(FirmwareDistributionThread.class);

    private final KafkaNetworkReceiver<FirmwareRetrievalMessage> firmwareReceiver;

    private final FileTransferHandler fileTransferHandler;
    private final Path workingDirectory;
    private final int id;

    public FirmwareDistributionThread(Path workingDirectory, int id) {
        logger.info("Intializing thread " + id);
        this.workingDirectory = workingDirectory;
        this.id = id;
        this.firmwareReceiver = new KafkaNetworkReceiver<>(
                new FirmwareRetrievalMessageDeserializer(),
                KafkaTopic.FIRMWARE_RETRIEVAL,
                KafkaConstants.CLIENT_ID
        );
        this.fileTransferHandler = new SCPFileTransferHandler(workingDirectory);
    }

    @Override
    public void run() {
        while (true) {
            final FirmwareRetrievalMessage retrievalMessage = firmwareReceiver.receive();

            try {
                logRetrievalIntent(retrievalMessage);

                fileTransferHandler.upload(
                        new NodeTransferTarget(retrievalMessage.hostName, retrievalMessage.userName, retrievalMessage.targetPath),
                        getValidFirmwarePath(retrievalMessage)
                );

                logRetrievalSuccess(retrievalMessage);
            } catch (Exception e) {
                logger.error(String.format(
                        "[Experiment %d] Failed to execute firmware for node %s transfer due to %s",
                        retrievalMessage.experimentId,
                        retrievalMessage.nodeId,
                        e.getMessage()
                ));
            }
        }
    }

    private void logRetrievalSuccess(FirmwareRetrievalMessage retrievalMessage) {
        logger.info(String.format(
                "[Thread %d] [Experiment %d] Node %s got firmware %s",
                id,
                retrievalMessage.experimentId,
                retrievalMessage.nodeId,
                retrievalMessage.firmwareName
        ));
    }

    private void logRetrievalIntent(FirmwareRetrievalMessage retrievalMessage) {
        logger.info(String.format(
                "[Thread %d] [Experiment %d] Node %s requests firmware transfer",
                id,
                retrievalMessage.experimentId,
                retrievalMessage.nodeId
        ));
    }

    private Path getValidFirmwarePath(FirmwareRetrievalMessage retrievalMessage) throws IOException {
        return getValidFirmwarePath(retrievalMessage.experimentId, retrievalMessage.firmwareName);
    }

    private Path getValidFirmwarePath(long experimentId, String firmwareName) throws IOException {
        final Path experimentFolder = PathUtil.getExperimentPath(experimentId);

        if (!Files.isDirectory(experimentFolder))
            throw new IOException("Experiment folder for experiment " + experimentId + " does not exist!");

        final Path firmwareFolder = PathUtil.getFirmwarePath(experimentId);

        if (!Files.isDirectory(firmwareFolder))
            throw new IOException("Experiment " + experimentId + " has no firmware folder!");

        final Path firmware = firmwareFolder.resolve(firmwareName);

        if (!Files.isRegularFile(firmware))
            throw new IOException("Firmware " + firmwareName + " does not exist for experiment " + experimentId);

        return firmware;
    }
}
