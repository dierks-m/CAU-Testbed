import datetime
import os
import sched, time

from typing import List

import experiment.modules.module
from configuration import nodeConfiguration
from configuration.experiment import Experiment, ExperimentModule, ModuleType
from experiment.modules.zoul import ZoulExperimentModule
from network import firmware

scheduler = sched.scheduler(time.time, time.sleep)


def module_factory(experiment_id: str, module: ExperimentModule) -> experiment.modules.module.ExperimentModule:
    if module.id == "ZOUL":
        return ZoulExperimentModule(
            os.path.join(
                firmware.resolve_local_fw_path(nodeConfiguration.configuration.workingDirectory, experiment_id),
                module.firmware
            ),
            os.path.join(nodeConfiguration.configuration.workingDirectory, experiment_id, "logs", "zoul.log")
        )

    return None


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

    def wait_for_firmware(self, target_time: datetime.datetime):
        modules = self.get_modules()

        for module in modules:
            firmware_file_path = os.path.join(
                firmware.resolve_local_fw_path(nodeConfiguration.configuration.workingDirectory, self.experiment.experiment_id),
                module.firmware
            )

            while not os.path.exists(firmware_file_path):
                time.sleep(1)

                if datetime.datetime.now() >= target_time:
                    raise RuntimeError("Failed to retrieve firmware in time!")

            print(f"Got firmware '{module.firmware}'")

    def initiate(self):
        # Initiate firmware retrieval and wait either until 30 seconds before experiment (scheduled)
        # or a maximum of 30 seconds from now (in case of immediate/late execution)
        self.retrieve_firmware()
        scheduler.enter(0, 0, lambda: self.wait_for_firmware(max(self.experiment.start - datetime.timedelta(seconds=30), datetime.datetime.now() + datetime.timedelta(seconds=30))))

        modules = self.get_modules()

        for module in modules:
            wrapped_module = module_factory(self.experiment.experiment_id, module)

            if wrapped_module is not None:
                scheduler.enter(0, 1, wrapped_module.prepare) # Prepare right after firmware arrives (e.g. BSL address etc.)

                # Enter start and stop times for the individual modules
                scheduler.enterabs(max(self.experiment.start, datetime.datetime.now()).timestamp(), 1, wrapped_module.start)
                scheduler.enterabs(max(self.experiment.end, datetime.datetime.now()).timestamp(), 1, wrapped_module.stop)



        scheduler.run()