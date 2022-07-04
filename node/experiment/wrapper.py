import datetime
import sched, time

from typing import List

import experiment.modules.module
from configuration import nodeConfiguration
from configuration.experiment import Experiment, ExperimentModule, ModuleType
from experiment.modules.zoul import ZoulExperimentModule
from network import firmware

scheduler = sched.scheduler(time.time, time.sleep)

def module_factory(experiment_id: str, module: ExperimentModule) ->  experiment.modules.module.ExperimentModule:
    if module.id == "ZOUL":
        return ZoulExperimentModule(firmware.resolve_local_fw_path(nodeConfiguration.configuration.workingDirectory, experiment_id, module.firmware))

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
        # scheduler.enterabs(self.experiment.start.timestamp(), 0, lambda _: print("Experiment starts now!"), ("abc",))
        # scheduler.enterabs(self.experiment.end.timestamp(), 0, lambda _: print("Experiment ends now!"), ("abc",))

        module = module_factory(self.experiment.experiment_id, self.get_modules()[0])

        scheduler.enterabs(self.experiment.start.timestamp(), 0, module.start)
        scheduler.enterabs(self.experiment.end.timestamp(), 0, module.stop)

        scheduler.run()
        print("Scheduler done")