package de.cau.testbed.server.network.fileTransfer;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;
import java.nio.file.Path;

public class SCPFileTransferHandler implements FileTransferHandler {
    @Override
    public void upload(TransferTarget target, Path localPath) throws IOException {
        final SSHClient sshClient = createSSHConnection(target.host());

        try {
            sshClient.authPublickey(target.user());
            sshClient.newSCPFileTransfer().upload(
                    localPath.toString(),
                    target.path().toString()
            );
        } finally {
            sshClient.disconnect();
        }
    }

    @Override
    public void download(TransferTarget target, Path localPath) throws IOException {
        final SSHClient sshClient = createSSHConnection(target.host());

        try {
            sshClient.authPublickey(target.user());
            sshClient.newSCPFileTransfer().download(
                    target.path().toString(),
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
}
