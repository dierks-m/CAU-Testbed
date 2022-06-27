from configuration import nodeConfiguration
from network.experimentProcessor import ExperimentProcessor
from network.firmware import FirmwareRetriever
from network.heartbeat import HeartbeatThread
from pathlib import Path

from network.log import LogTransfer

if __name__ == '__main__':
    config = nodeConfiguration.load_configuration(Path("./config/node-configuration.yaml"))
    # HeartbeatThread(config.id, config.bootstrapAddress, 10).start()


    # firmwareRetriever = FirmwareRetriever(config.wireguardAddress, config.workingDirectory, config.bootstrapAddress)
    # print("Retrieving zoul-test.zoul firmware...")
    # firmwareRetriever.retrieve_firmware("1234567890", "zoul-test.zoul")

    # logSender = LogTransfer(config.wireguardAddress, config.id, config.workingDirectory, config.bootstrapAddress)
    # print("Sending logs")
    # logSender.initiateLogRetrieval("1234567890")

    processor = ExperimentProcessor(config.id, config.bootstrapAddress)
    processor.start()