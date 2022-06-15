import json
import time

import constants
from threading import Thread
from kafka import KafkaProducer

class HeartbeatMessage:
    def __init__(self, nodeId: str):
        self.nodeId = nodeId

class HeartbeatThread(Thread):
    def __init__(self, nodeId: str, bootstrapAddress: str, interval: int):
        Thread.__init__(self)
        self.nodeId = nodeId
        self.interval = interval
        self.kafkaConnector = KafkaProducer(
            bootstrap_servers=bootstrapAddress,
            # key_serializer=str.encode,
            value_serializer=lambda x: json.dumps(x, default=lambda o: o.__dict__).encode('utf-8')
        )

    def run(self):
        while (True):
            self.kafkaConnector.send(constants.HEARTBEAT_TOPIC, HeartbeatMessage(self.nodeId))
            time.sleep(self.interval - 1)