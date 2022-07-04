from pathlib import Path


class ExperimentModule:
    def __init__(self, firmware_path: Path):
        self.firmware_path = firmware_path

    def prepare(self):
        pass # Can be used to e.g. extract firmware addresses and such

    def start(self):
        pass # To be defined by subclasses!

    def stop(self):
        pass # To be defined by subclasses!

