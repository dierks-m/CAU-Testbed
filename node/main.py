from configuration import nodeConfiguration
from network.firmware import FirmwareRetriever
from network.heartbeat import HeartbeatThread
from pathlib import Path

if __name__ == '__main__':
    config = nodeConfiguration.load_configuration(Path("./config/node-configuration.yaml"))
    HeartbeatThread(config.id, config.bootstrapAddress, 10).start()


    firmwareRetriever = FirmwareRetriever(config.wireguardAddress, config.workingDirectory, config.bootstrapAddress)
    print("Retrieving zoul-test.zoul firmware...")
    firmwareRetriever.retrieve_firmware("1234567890", "zoul-test.zoul")
