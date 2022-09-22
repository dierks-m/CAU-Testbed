import os
import shutil
import subprocess
import threading
from logging import Logger
import configuration.nodeConfiguration as node_configuration

import network.log


class GPIOTracer:
    """
    Class to wrap node's GPIO tracer into a single object that manages access to its start and stop commands
    to that no race conditions arise.
    """

    def __init__(self):
        self._lock = threading.Lock()
        self.owner = None
        self.logger = None

    def start(self, experiment_id: int, logger: Logger):
        with self._lock:
            if self.owner is not None:
                logger.warning("Tried to start GPIO trace, but other experiment still runs trace")
                return

            self.owner = threading.current_thread()
            self.logger = logger

        self.logger.info("Starting GPIO trace")
        self._start_gpio_tracer(experiment_id)

    def stop(self):
        with self._lock:
            if self.owner != threading.current_thread():
                return

            self.logger.info("Stopping GPIO trace")
            os.system(f'gpiotc --stop > /dev/null')
            self.owner = None
            self.logger = None

    def _start_gpio_tracer(self, experiment_id: int):
        gpiotc_path = shutil.which("gpiotc")

        if gpiotc_path is None:
            self.logger.warning("Cannot start GPIO tracer: gpiotc not in PATH")
            return

        gpio_output_dir = node_configuration.configuration.workingDirectory.joinpath(str(experiment_id), "logs")
        gpio_tracer_output = ""

        process = subprocess.Popen(f'{gpiotc_path} --start --tracedir {gpio_output_dir}', shell=True, stdout=subprocess.PIPE)
        for line in process.stdout:
            gpio_tracer_output += line.decode(encoding='utf-8', errors='ignore')

        process.wait()

        if gpio_tracer_output.find("started collection on device") > 0:
            self.logger.info("Successfully started GPIO trace")
        else:
            self.logger.warning("Failed to start GPIO trace")