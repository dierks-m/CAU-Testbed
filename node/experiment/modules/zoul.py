import os

from experiment.modules.module import ExperimentModule


class ZoulExperimentModule(ExperimentModule):
    def __init__(self, *args, **kwargs):
        super(ZoulExperimentModule, self).__init__(*args, **kwargs)
        self.bsl_address_path = self.firmware_path.with_name(self.firmware_path.name + "-bsl-address.txt")


    def prepare(self):
        self.logger.info("Preparing ZOUL module")

        os.system("arm-none-eabi-objdump -h %s |"
                  "grep -B1 LOAD | grep -Ev 'LOAD|\\-\\-' |"
                  "awk '{print \"0x\" $5}' | sort -g | head -1 > %s" % (str(self.firmware_path), str(self.bsl_address_path)))


        # print(f"BSL address is {self.bsl_address_path}")

        os.system(
            "arm-none-eabi-objcopy -O binary --gap-fill 0xff %s %s" % (str(self.firmware_path), str(self.firmware_path) + ".bin")
        )

    def start(self):
        self.logger.info("Starting ZOUL module")
        
        os.system("scripts/zoul/install.sh %s %s" % (str(self.firmware_path) + ".bin", str(self.bsl_address_path)))

        if self.serial_forward:
            os.system("scripts/zoul/serial_forwarder.sh %s" % (self.log_path))
        else:
            os.system("scripts/zoul/serialdump.sh %s" % (self.log_path))

    def stop(self):
        self.logger.info("Stopping ZOUL module")
        os.system("scripts/zoul/stop-forwarder-dump.sh")

        # Install null firmware to get device to a known state
        os.system("scripts/zoul/install.sh scripts/zoul/null.bin scripts/zoul/null_bsl_address.txt")
        self.logger.info("Stopped ZOUL module")
