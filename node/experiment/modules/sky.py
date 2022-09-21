import os

from experiment.modules.module import ExperimentModule


class SkyExperimentModule(ExperimentModule):
    def prepare(self):
        self.logger.info("Preparing SKY module")

    def start(self):
        self.logger.info("Starting SKY module")
        os.system("scripts/sky/install.sh %s" % str(self.firmware_path))

        if self.serial_forward:
            os.system("scripts/sky/serial-forwarder.sh %s" % str(self.firmware_path))
        else:
            os.system("scripts/sky/serial-dump.sh %s" % str(self.log_path))

        self.logger.info("Started SKY module")

    def stop(self):
        self.logger.info("Stopping SKY module")
        os.system("scripts/sky/stop-forwarder-dump.sh")

        # Install null firmware to get device to a known state
        os.system("scripts/sky/install.sh scripts/sky/null.sky.ihex")
        self.logger.info("Stopped SKY module")