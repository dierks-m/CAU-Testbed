import datetime
import logging
import os
import sched
import time
from typing import List

import experiment.modules.module
from configuration import nodeConfiguration
from configuration.experiment import Experiment, ExperimentModule
from experiment.modules.zoul import ZoulExperimentModule
from network import firmware, log

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

        log.transfer_handler.create_logging_directory(self.experiment.experiment_id)

        logging.basicConfig(
            filename=os.path.join(nodeConfiguration.configuration.workingDirectory, experiment.experiment_id, "logs", "node.log"),
            level=logging.INFO,
            format=f'%(asctime)s.%(msecs)03d [%(levelname)s] [{nodeConfiguration.configuration.id}] [Experiment {self.experiment.experiment_id}] %(message)s',
            datefmt='%H:%M:%S'
        )

    def get_modules(self) -> List[ExperimentModule]:
        for node in self.experiment.nodes:
            if node.id == self.node_id:
                return node.modules

        return []

    def retrieve_firmware(self):
        logging.debug("Initiating firmware retrieval")

        modules = self.get_modules()

        for module in modules:
            nodeConfiguration.firmware_retriever.retrieve_firmware(self.experiment.experiment_id, module.firmware)

    def wait_for_firmware(self, target_time: datetime.datetime):
        logging.info("Waiting for firmware")

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

            # Perhaps improve this with a message back from the server
            # If this size check isn't used, this will terminate immediately as soon as the file is created, but the firmware
            # isn't transferred yet
            firmware_file_size = -1
            while firmware_file_size < (firmware_file_size := os.path.getsize(firmware_file_path)):
                time.sleep(.1)

            print(f"Got firmware '{module.firmware}'")
            logging.info(f"Got firmware '{module.firmware}'")

        logging.info("All firmware received")

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

        # Initiate log retrieval for *all* modules
        scheduler.enterabs(max(self.experiment.end, datetime.datetime.now()).timestamp(), 2,
                           lambda: log.transfer_handler.initiate_log_retrieval(self.experiment.experiment_id))

        scheduler.enterabs(max(self.experiment.end, datetime.datetime.now()).timestamp(), 3,
                           lambda: logging.shutdown())

        scheduler.run()