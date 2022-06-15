from configuration import nodeConfiguration
from network.heartbeat import HeartbeatThread
from pathlib import Path

if __name__ == '__main__':
    config = nodeConfiguration.load_configuration(Path("./config/node-configuration.yaml"))
    HeartbeatThread(config.id, config.bootstrapAddress, 10).start()
