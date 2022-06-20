package de.cau.testbed.server.module;

import de.cau.testbed.server.constants.KafkaConstants;
import de.cau.testbed.server.constants.KafkaTopic;
import de.cau.testbed.server.network.FirmwareRetrievalMessage;
import de.cau.testbed.server.network.KafkaNetworkReceiver;
import de.cau.testbed.server.network.fileTransfer.FileTransferHandler;
import de.cau.testbed.server.network.fileTransfer.SCPFileTransferHandler;
import de.cau.testbed.server.network.serialization.FirmwareRetrievalMessageDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class FirmwareDistributionThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(FirmwareDistributionThread.class);

    private final Path workingDirectory;
    private final KafkaNetworkReceiver<FirmwareRetrievalMessage> firmwareReceiver;

    private final FileTransferHandler fileTransferHandler;

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
                fileTransferHandler.transfer(retrievalMessage);
                logger.info(String.format(
                        "Transferred firmware %s to node %s",
                        retrievalMessage.firmwareName, retrievalMessage.hostName
                ));
            } catch (Exception e) {
                logger.error("Failed to execute firmware transfer due to ", e);
            }
        }
    }
}
