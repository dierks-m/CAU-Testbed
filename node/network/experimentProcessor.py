import json
import logging
import threading
import typing
from threading import Thread

from kafka import KafkaConsumer

from configuration import constants
from configuration.experiment import Experiment, InvocationMethod
from experiment.wrapper import ExperimentWrapper


def node_in_experiment(node_id: str, experiment: Experiment) -> bool:
    for node in experiment.nodes:
        if node.id == node_id:
            return True

    return False


class ExperimentTracker:
    def __init__(self):
        self.running_experiments = {}

    def add_experiment(self, id: int, experiment: ExperimentWrapper):
        self.running_experiments[id]: typing.Dict[int, ExperimentWrapper] = experiment

    def cancel_experiment(self, id: int):
        if id not in self.running_experiments:
            return

        self.running_experiments[id].cancel()

    def stop_experiment(self, id: int):
        if id not in self.running_experiments:
            return

        self.running_experiments[id].stop()

    def cleanup(self, experiment: ExperimentWrapper):
        for id, wrapper in self.running_experiments.items():
            if wrapper == experiment:
                del self.running_experiments[id]
                return


class ExperimentProcessor(ExperimentTracker, Thread):
    def __init__(self, node_id: str, bootstrap_address: str):
        ExperimentTracker.__init__(self)
        Thread.__init__(self)
        self.node_id = node_id
        self.kafka_connector = KafkaConsumer(
            bootstrap_servers=bootstrap_address,
            group_id=node_id,
            value_deserializer=lambda x: Experiment.from_json(json.loads(x.decode('utf-8')))
        )

        self.kafka_connector.subscribe(topics=[constants.EXPERIMENT_PREPARATION_TOPIC])

    def run(self):
        for message in self.kafka_connector:
            experiment = message.value

            if experiment.action == InvocationMethod.START:
                if node_in_experiment(self.node_id, experiment):
                    wrapper = ExperimentWrapper(self.node_id, experiment, lambda: self.cleanup(experiment))
                    threading.Thread(target=wrapper.run).start()

                    self.add_experiment(experiment.experiment_id, wrapper)
            elif experiment.action == InvocationMethod.CANCEL:
                logging.info(f'Cancelling experiment {experiment.experiment_id}')
                self.cancel_experiment(experiment.experiment_id)
            elif experiment.action == InvocationMethod.STOP:
                logging.info(f'Stopping experiment {experiment.experiment_id} early')
                self.stop_experiment(experiment.experiment_id)
