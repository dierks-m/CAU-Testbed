import os

from experiment.modules.module import ExperimentModule


class NRF52ExperimentModule(ExperimentModule):
    def prepare(self):
        self.logger.info("Preparing NRF52 module")

    def start(self):
        self.logger.info("Starting NRF52 module")
        os.system("scripts/nrf52/install.sh %s" % str(self.firmware_path))

        if self.serial_forward:
            self.logger.info("Serial forward not defined for NRF52 module. Doing serial dump.")

        os.system("scripts/nrf52/serial-dump.sh %s" % str(self.firmware_path))
        self.logger.info("Started NRF52 module")

    def stop(self):
        self.logger.info("Stopping NRF52 module")
        os.system("scripts/nrf52/stop-forwarder-dump.sh")

        # Install null firmware to get device to a known state
        os.system("scripts/nrf52/install.sh scripts/sky/null.nrf52.hex")
        self.logger.info("Stopped NRF52 module")