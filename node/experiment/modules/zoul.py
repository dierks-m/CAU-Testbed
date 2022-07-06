import os
from pathlib import Path

from experiment.modules.module import ExperimentModule


class ZoulExperimentModule(ExperimentModule):
    def __init__(self, firmware_path: Path):
        ExperimentModule.__init__(self, firmware_path)
        self.bsl_address = "dummy"


    def prepare(self):
        self.bsl_address = os.system("arm-none-eabi-objdump -h %s |"
                                     "grep -B1 LOAD | grep -Ev 'LOAD|\\-\\-' |"
                                     "awk '{print \"0x\" $5}' | sort -g | head -1" % (self.firmware_path,))

        print(f"BSL address is {self.bsl_address}")

        os.system(
            "arm-none-eabi-objcopy -0 binary --gap-fill 0xff %s %s" % (self.firmware_path, self.firmware_path + ".bin")
        )

    def start(self):
        os.system("scripts/zoul/install.sh %s %s" % (self.firmware_path + ".bin", self.bsl_address))

    def stop(self):
        os.system("scripts/zoul/install.sh scripts/zoul/null.bin scripts/zoul/null_bsl_address.txt")
