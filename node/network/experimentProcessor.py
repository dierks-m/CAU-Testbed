import json
from threading import Thread

from kafka import KafkaConsumer

from configuration import constants
from configuration.experiment import Experiment, ExperimentNode
from experiment.wrapper import ExperimentWrapper


def get_matching_experiment_node(node_id: str, experiment: Experiment) -> ExperimentNode:
    for node in experiment.nodes:
        if node.id == node_id:
            return node

    return None


class ExperimentProcessor(Thread):
    def __init__(self, node_id: str, bootstrap_address: str):
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
            wrapper = ExperimentWrapper(self.node_id, experiment)
            wrapper.initiate()