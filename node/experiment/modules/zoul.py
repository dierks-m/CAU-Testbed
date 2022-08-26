import logging
import os
from pathlib import Path

from experiment.modules.module import ExperimentModule


class ZoulExperimentModule(ExperimentModule):
    def __init__(self, *args, **kwargs):
        super(ZoulExperimentModule, self).__init__(self, *args, **kwargs)
        self.bsl_address_path = self.firmware_path + "-bsl-address.txt"


    def prepare(self):
        logging.info("Preparing ZOUL module")

        os.system("arm-none-eabi-objdump -h %s |"
                  "grep -B1 LOAD | grep -Ev 'LOAD|\\-\\-' |"
                  "awk '{print \"0x\" $5}' | sort -g | head -1 > %s" % (self.firmware_path, self.bsl_address_path))


        # print(f"BSL address is {self.bsl_address_path}")

        os.system(
            "arm-none-eabi-objcopy -O binary --gap-fill 0xff %s %s" % (self.firmware_path, self.firmware_path + ".bin")
        )

    def start(self):
        logging.info("Starting ZOUL module")
        os.system("scripts/zoul/install.sh %s %s" % (self.firmware_path + ".bin", self.bsl_address_path))

        if self.serial_forward:
            os.system("scripts/zoul/serial_forwarder.sh %s" % (self.log_path))
        else:
            os.system("scripts/zoul/serialdump.sh %s" % (self.log_path))

    def stop(self):
        logging.info("Stopping ZOUL module")
        os.system("scripts/zoul/stop-forwarder-dump.sh")

        # Install null firmware to get device to a known state
        os.system("scripts/zoul/install.sh scripts/zoul/null.bin scripts/zoul/null_bsl_address.txt")
