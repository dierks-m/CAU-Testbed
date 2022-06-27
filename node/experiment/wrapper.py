from typing import List

from configuration import nodeConfiguration
from configuration.experiment import Experiment, ExperimentModule


class ExperimentWrapper:
    def __init__(self, node_id: str, experiment: Experiment):
        self.node_id = node_id
        self.experiment = experiment

    def get_modules(self) -> List[ExperimentModule]:
        for node in self.experiment.nodes:
            if node.id == self.node_id:
                return node.modules

        return []

    def retrieve_firmware(self):
        modules = self.get_modules()

        for module in modules:
            nodeConfiguration.firmware_retriever.retrieve_firmware(self.experiment.experiment_id, module.firmware)

    def initiate(self):
        self.retrieve_firmware()