import json
import os
from pathlib import Path

from kafka import KafkaProducer

from configuration import constants


class FirmwareRetrievalMessage:
    def __init__(self, experimentId: str, firmwareName: str, hostName: str, targetPath: str):
        self.experimentId = experimentId
        self.firmwareName = firmwareName
        self.hostName = hostName
        self.userName = os.getlogin()
        self.targetPath = targetPath

def resolve_local_fw_path(working_directory: str, experiment_id: str, firmware_name: str) -> Path:
    return os.path.join(working_directory, experiment_id, "firmware")

class FirmwareRetriever():
    def __init__(self, host_name: str, working_directory: str, kafka_bootstrap: str):
        self.host_name = host_name
        self.working_directory = working_directory
        self.retrieval_msg_producer = KafkaProducer(
            bootstrap_servers=kafka_bootstrap,
            value_serializer=lambda x: json.dumps(x, default=lambda o: o.__dict__).encode("utf-8")
        )

    def retrieve_firmware(self, experiment_id: str, firmware_name: str):
        local_fw_path = resolve_local_fw_path(self.working_directory, experiment_id, firmware_name)
        os.makedirs(local_fw_path, exist_ok=True)

        self.retrieval_msg_producer.send(
            constants.FIRMWARE_RETRIEVAL_TOPIC,
            FirmwareRetrievalMessage(
                experiment_id,
                firmware_name,
                self.host_name,
                resolve_local_fw_path(self.working_directory, experiment_id, firmware_name)
            )
        )