import logging
import os
from pathlib import Path

from experiment.modules.module import ExperimentModule


class ZoulExperimentModule(ExperimentModule):
    def __init__(self, firmware_path: Path, log_path: Path):
        ExperimentModule.__init__(self, firmware_path, log_path)
        self.bsl_address_path = "dummy"


    def prepare(self):
        logging.debug("Preparing ZOUL module")
        self.bsl_address_path = self.firmware_path + "-bsl-address.txt"

        os.system("arm-none-eabi-objdump -h %s |"
                  "grep -B1 LOAD | grep -Ev 'LOAD|\\-\\-' |"
                  "awk '{print \"0x\" $5}' | sort -g | head -1 > %s" % (self.firmware_path, self.bsl_address_path))


        # print(f"BSL address is {self.bsl_address_path}")

        os.system(
            "arm-none-eabi-objcopy -O binary --gap-fill 0xff %s %s" % (self.firmware_path, self.firmware_path + ".bin")
        )

    def start(self):
        logging.debug("Starting ZOUL module")
        os.system("scripts/zoul/install.sh %s %s" % (self.firmware_path + ".bin", self.bsl_address_path))
        os.system("scripts/zoul/serialdump.sh %s" % (self.log_path))

    def stop(self):
        logging.debug("Stopping ZOUL module")
        os.system("scripts/zoul/serialdump-stop.sh")
        os.system("scripts/zoul/install.sh scripts/zoul/null.bin scripts/zoul/null_bsl_address.txt")
