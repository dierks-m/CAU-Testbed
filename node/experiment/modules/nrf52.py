import logging
import os

from experiment.modules.module import ExperimentModule


class NRF52ExperimentModule(ExperimentModule):
    def prepare(self):
        logging.info("Preparing NRF52 module")

    def start(self):
        logging.info("Starting NRF52 module")
        os.system("scripts/nrf52/install.sh %s" % (self.firmware_path))

        if self.serial_forward:
            logging.info("Serial forward not defined for NRF52 module. Doing serial dump.")

        os.system("scripts/nrf52/serial-dump.sh %s" % (self.firmware_path))

    def stop(self):
        logging.info("Stopping NRF52 module")
        os.system("scripts/nrf52/stop-forwarder-dump.sh")

        # Install null firmware to get device to a known state
        os.system("scripts/nrf52/install.sh scripts/sky/null.nrf52.hex")