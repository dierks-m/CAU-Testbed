import os.path

from configuration import nodeConfiguration
from configuration.experiment import Experiment


def create_logging_directory(experiment: Experiment):
    os.makedirs(os.path.join(nodeConfiguration.configuration.workingDirectory, experiment.experiment_id, "logs"), exist_ok=True)
