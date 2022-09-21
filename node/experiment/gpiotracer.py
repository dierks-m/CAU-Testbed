import os
import threading
from logging import Logger
from configuration.nodeConfiguration import configuration

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
        os.system(f'gpiotc --start --tracedir {network.log.resolve_local_log_path(configuration.workingDirectory, str(experiment_id))}')

    def stop(self):
        with self._lock:
            if self.owner != threading.current_thread():
                raise RuntimeError("Tried to stop GPIO trace of other experiment thread")

            self.logger.info("Stopping GPIO trace")
            os.system(f'gpiotc --stop')
            self.owner = None
            self.logger = None
