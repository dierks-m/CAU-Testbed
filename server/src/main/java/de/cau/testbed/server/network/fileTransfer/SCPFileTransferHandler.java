package de.cau.testbed.server.network.fileTransfer;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.IOException;
import java.nio.file.Path;

public class SCPFileTransferHandler implements FileTransferHandler {
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
}
