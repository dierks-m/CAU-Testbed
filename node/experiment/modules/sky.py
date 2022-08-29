import logging
import os

from experiment.modules.module import ExperimentModule


class SkyExperimentModule(ExperimentModule):
    def prepare(self):
        logging.info("Preparing SKY module")

    def start(self):
        logging.info("Starting SKY module")
        os.system("scripts/sky/install.sh %s" % str(self.firmware_path))

        if self.serial_forward:
            os.system("scripts/sky/serial-forwarder.sh %s" % str(self.firmware_path))
        else:
            os.system("scripts/sky/serial-dump.sh %s" % str(self.firmware_path))

    def stop(self):
        logging.info("Stopping SKY module")
        os.system("scripts/sky/stop-forwarder-dump.sh")

        # Install null firmware to get device to a known state
        os.system("scripts/sky/install.sh scripts/sky/null.sky.ihex")