package de.cau.testbed.server.network.fileTransfer;

import de.cau.testbed.server.PathUtil;
import de.cau.testbed.server.network.message.FirmwareRetrievalMessage;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SCPFileTransferHandler implements FileTransferHandler {
    private final Path workingDirectory;

    public SCPFileTransferHandler(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public void transfer(FirmwareRetrievalMessage retrievalMessage) throws IOException {
        final SSHClient sshClient = createSSHConnection(retrievalMessage.hostName);

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

    @Override
    public void upload(TransferTarget target, Path localPath) throws IOException {
        final SSHClient sshClient = createSSHConnection(target.getHost());

        try {
            sshClient.authPublickey(target.getUser());
            sshClient.newSCPFileTransfer().upload(
                    localPath.toString(),
                    target.getPath().toString()
            );
        } finally {
            sshClient.disconnect();
        }
    }

    @Override
    public void download(TransferTarget target, Path localPath) throws IOException {
        final SSHClient sshClient = createSSHConnection(target.getHost());

        try {
            sshClient.authPublickey(target.getUser());
            sshClient.newSCPFileTransfer().download(
                    target.getPath().toString(),
                    localPath.toString()
            );
        } finally {
            sshClient.disconnect();
        }
    }

    private SSHClient createSSHConnection(String target) throws IOException {
        final SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.connect(target);

        return sshClient;
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
