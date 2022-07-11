from pathlib import Path

from configuration import nodeConfiguration
from network import log
from network.experimentProcessor import ExperimentProcessor
from network.firmware import FirmwareRetriever

if __name__ == '__main__':
    config = nodeConfiguration.load_configuration(Path("./config/node-configuration.yaml"))
    nodeConfiguration.configuration = config
    # HeartbeatThread(config.id, config.bootstrapAddress, 10).start()

    nodeConfiguration.firmware_retriever = FirmwareRetriever(config.wireguardAddress, config.workingDirectory,
                                                             config.bootstrapAddress)
    # print("Retrieving zoul-test.zoul firmware...")
    # firmwareRetriever.retrieve_firmware("1234567890", "zoul-test.zoul")

    log.transfer_handler = log.LogTransfer(config.wireguardAddress, config.id, config.workingDirectory, config.bootstrapAddress)

    processor = ExperimentProcessor(config.id, config.bootstrapAddress)
    processor.start()
