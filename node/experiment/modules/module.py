from pathlib import Path


class ExperimentModule:
    def __init__(self,
                 firmware: Path,
                 log_path: Path,
                 serial_dump: bool,
                 serial_forward: bool,
                 gpio_tracer: bool
                 ):
        self.firmware_path = firmware
        self.log_path = log_path
        self.serial_dump = serial_dump
        self.serial_forward = serial_forward
        self.gpio_tracer = gpio_tracer

    def prepare(self):
        pass  # Can be used to e.g. extract firmware addresses and such

    def start(self):
        pass  # To be defined by subclasses!

    def stop(self):
        pass  # To be defined by subclasses!
