import os
from pathlib import Path

from experiment.modules.module import ExperimentModule


class ZoulExperimentModule(ExperimentModule):
    def __init__(self, firmware_path: Path):
        ExperimentModule.__init__(self, firmware_path)
        self.bsl_address_path = "dummy"


    def prepare(self):
        self.bsl_address_path = self.firmware_path + "-bsl-address.txt"

        os.system("arm-none-eabi-objdump -h %s |"
                  "grep -B1 LOAD | grep -Ev 'LOAD|\\-\\-' |"
                  "awk '{print \"0x\" $5}' | sort -g | head -1 > %s" % (self.firmware_path, self.bsl_address_path))


        print(f"BSL address is {self.bsl_address_path}")

        os.system(
            "arm-none-eabi-objcopy -O binary --gap-fill 0xff %s %s" % (self.firmware_path, self.firmware_path + ".bin")
        )

    def start(self):
        os.system("scripts/zoul/install.sh %s %s" % (self.firmware_path + ".bin", self.bsl_address_path))

    def stop(self):
        os.system("scripts/zoul/install.sh scripts/zoul/null.bin scripts/zoul/null_bsl_address.txt")
