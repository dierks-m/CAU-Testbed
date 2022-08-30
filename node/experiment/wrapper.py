import datetime
import logging
import os
import sched
import time
from pathlib import Path
from typing import List

import experiment.modules.module
from configuration import nodeConfiguration
from configuration.experiment import Experiment, ExperimentModule, ModuleType
from experiment.modules.nrf52 import NRF52ExperimentModule
from experiment.modules.sky import SkyExperimentModule
from experiment.modules.zoul import ZoulExperimentModule
from network import firmware, log


def module_factory(experiment_id: str, module: ExperimentModule) -> experiment.modules.module.ExperimentModule:
    if module.serial_dump:
        log_path_prefix = nodeConfiguration.configuration.workingDirectory.joinpath(experiment_id, "logs")
    else:
        log_path_prefix = Path("/tmp", "tesbed", experiment_id)

    if module.id == ModuleType.ZOUL:
        return ZoulExperimentModule(
            firmware=firmware.resolve_local_fw_path(
                nodeConfiguration.configuration.workingDirectory, experiment_id
            ).joinpath(module.firmware),
            log_path=log_path_prefix.joinpath("zoul.log"),
            serial_dump=module.serial_dump,
            serial_forward=module.serial_forward,
            gpio_tracer=module.gpio_tracer
        )
    elif module.id == ModuleType.SKY:
        return SkyExperimentModule(
            firmware=firmware.resolve_local_fw_path(
                nodeConfiguration.configuration.workingDirectory, experiment_id
            ).joinpath(module.firmware),
            log_path=log_path_prefix.joinpath("sky.log"),
            serial_dump=module.serial_dump,
            serial_forward=module.serial_forward,
            gpio_tracer=module.gpio_tracer
        )
    elif module.id == ModuleType.NRF52:
        return NRF52ExperimentModule(
            firmware=firmware.resolve_local_fw_path(
                nodeConfiguration.configuration.workingDirectory, experiment_id
            ).joinpath(module.firmware),
            log_path=log_path_prefix.joinpath("nrf52.log"),
            serial_dump=module.serial_dump,
            serial_forward=module.serial_forward,
            gpio_tracer=module.gpio_tracer
        )

    return None


class ExperimentWrapper:
    def __init__(self, tracker, node_id: str, experiment: Experiment):
        self.tracker = tracker
        self.scheduler = sched.scheduler(time.time, time.sleep)
        self.node_id = node_id
        self.experiment = experiment
        self.wrapped_modules = []

        self.event_list = []

        log.transfer_handler.create_logging_directory(self.experiment.experiment_id)

        logging.basicConfig(
            filename=os.path.join(nodeConfiguration.configuration.workingDirectory, experiment.experiment_id, "logs", "node.log"),
            level=logging.INFO,
            format=f'%(asctime)s.%(msecs)03d [%(levelname)s] [{nodeConfiguration.configuration.id}] [Experiment {self.experiment.experiment_id}] %(message)s',
            datefmt='%H:%M:%S',
            force=True
        )

    def get_modules(self) -> List[ExperimentModule]:
        for node in self.experiment.nodes:
            if node.id == self.node_id:
                return node.modules

        return []

    def retrieve_firmware(self):
        logging.info("Initiating firmware retrieval")

        modules = self.get_modules()

        for module in modules:
            nodeConfiguration.firmware_retriever.retrieve_firmware(self.experiment.experiment_id, module.firmware)

    def wait_for_firmware(self, target_time: datetime.datetime):
        logging.info("Waiting for firmware")

        modules = self.get_modules()

        for module in modules:
            firmware_file_path = os.path.join(
                firmware.resolve_local_fw_path(nodeConfiguration.configuration.workingDirectory,
                                               self.experiment.experiment_id),
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

    def cancel(self):
        for event in reversed(self.event_list):
            try:
                self.scheduler.cancel(event)
            except ValueError: # Event probably already got executed
                pass

        self.event_list.clear()

        for module in self.wrapped_modules:
            self.scheduler.enter(0, 0, module.stop)

        self.scheduler.enter(0, 1, logging.shutdown)
        self.scheduler.run()

    def initiate(self):
        # Initiate firmware retrieval and wait either until 30 seconds before experiment (scheduled)
        # or a maximum of 30 seconds from now (in case of immediate/late execution)
        self.retrieve_firmware()
        self.event_list.append(
            self.scheduler.enter(0, 0, lambda: self.wait_for_firmware(
                max(self.experiment.start - datetime.timedelta(seconds=30),
                    datetime.datetime.now() + datetime.timedelta(seconds=30))))
        )

        modules = self.get_modules()

        for module in modules:
            wrapped_module = module_factory(self.experiment.experiment_id, module)

            if wrapped_module is not None:
                self.event_list.append(
                    self.scheduler.enter(0, 1, wrapped_module.prepare)
                )  # Prepare right after firmware arrives (e.g. BSL address etc.)

                # Enter start and stop times for the individual modules
                self.event_list.append(
                    self.scheduler.enterabs(max(self.experiment.start, datetime.datetime.now()).timestamp(), 1,
                                            wrapped_module.start)
                )
                self.event_list.append(
                    self.scheduler.enterabs(max(self.experiment.end, datetime.datetime.now()).timestamp(), 1,
                                            wrapped_module.stop)
                )

                self.wrapped_modules.append(wrapped_module)

        # Initiate log retrieval for *all* modules
        end = max(self.experiment.end, datetime.datetime.now()).timestamp()
        self.event_list.append(
            self.scheduler.enterabs(end, 2,
                                    lambda: log.transfer_handler.initiate_log_retrieval(self.experiment.experiment_id))
        )

        self.event_list.append(
            self.scheduler.enterabs(end, 3, lambda: logging.shutdown())
        )

        self.scheduler.run()
        self.tracker.cleanup(self)
