import os
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

        gpio_output_dir = node_configuration.configuration.workingDirectory.joinpath(str(experiment_id), "logs")
        output_stream = os.popen(f'gpiotc --start --tracedir {gpio_output_dir}')

        try:
            command_output = output_stream.read()

            if command_output.find("started collection on device") > 0:
                self.logger.info("Successfully started GPIO trace")
            else:
                self.logger.warning("Failed to start GPIO trace")
        except Exception:
            self.logger.warning("Failed to read output of GPIO trace command. Execution may have failed")

        output_stream.close()

    def stop(self):
        with self._lock:
            if self.owner != threading.current_thread():
                return

            self.logger.info("Stopping GPIO trace")
            os.system(f'gpiotc --stop')
            self.owner = None
            self.logger = None
