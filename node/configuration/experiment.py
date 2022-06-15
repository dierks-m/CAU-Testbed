from enum import Enum
from typing import List

class ModuleType(Enum):
    NRF52 = 1
    ZOUL = 2
    SKY = 3

class ExperimentModule:
    def __init__(self, id: ModuleType, firmware: str):
        self.id = id
        self.firmware = firmware

class ExperimentNode:
    def __init__(self, id: str, modules: List[ExperimentModule]):
        self.modules = modules