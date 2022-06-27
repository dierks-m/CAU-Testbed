package de.cau.testbed.server.module;

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
import java.nio.file.Paths;

public class FirmwareDistributionThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(FirmwareDistributionThread.class);

    private final KafkaNetworkReceiver<FirmwareRetrievalMessage> firmwareReceiver;

    private final FileTransferHandler fileTransferHandler;
    private final Path workingDirectory;

    public FirmwareDistributionThread(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
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
                fileTransferHandler.upload(
                        new NodeTransferTarget(retrievalMessage.hostName, retrievalMessage.userName, retrievalMessage.targetPath),
                        getValidFirmwarePath(retrievalMessage)
                );

                logger.info(String.format(
                        "Transferred firmware %s to node %s",
                        retrievalMessage.firmwareName, retrievalMessage.hostName
                ));
            } catch (Exception e) {
                logger.error("Failed to execute firmware transfer due to ", e);
            }
        }
    }

    private Path getValidFirmwarePath(FirmwareRetrievalMessage retrievalMessage) throws IOException {
        return getValidFirmwarePath(retrievalMessage.experimentId, retrievalMessage.firmwareName);
    }

    private Path getValidFirmwarePath(String experimentId, String firmwareName) throws IOException {
        final Path experimentFolder = Paths.get(workingDirectory.toString(), experimentId);

        if (!Files.isDirectory(experimentFolder))
            throw new IOException("Experiment folder for experiment " + experimentId + " does not exist!");

        final Path firmwareFolder = Paths.get(experimentFolder.toString(), "firmware");

        if (!Files.isDirectory(firmwareFolder))
            throw new IOException("Experiment " + experimentId + " has no firmware folder!");

        final Path firmware = Paths.get(firmwareFolder.toString(), firmwareName);

        if (!Files.isRegularFile(firmware))
            throw new IOException("Firmware " + firmwareName + " does not exist for experiment " + experimentId);

        return firmware;
    }
}