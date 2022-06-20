package de.cau.testbed.server.network.fileTransfer;

import de.cau.testbed.server.network.message.FirmwareRetrievalMessage;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SCPFileTransferHandler implements FileTransferHandler {
    private final Path workingDirectory;

    public SCPFileTransferHandler(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public void transfer(FirmwareRetrievalMessage retrievalMessage) throws IOException {
        final SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.connect(retrievalMessage.hostName);

        try {
            sshClient.authPublickey(retrievalMessage.userName);
            sshClient.newSCPFileTransfer().upload(
                    getValidFirmwarePath(retrievalMessage.experimentId, retrievalMessage.firmwareName).toString(),
                    retrievalMessage.targetPath.toString()
            );
        } finally {
            sshClient.disconnect();
        }
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
