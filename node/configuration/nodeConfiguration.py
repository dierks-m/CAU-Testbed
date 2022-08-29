from ruamel.yaml import YAML, yaml_object, CommentedMap
from pathlib import Path

from network.firmware import FirmwareRetriever

yaml = YAML()


@yaml_object(yaml)
class Configuration:
    def __init__(
            self,
            id: str,
            bootstrapAddress: str,
            workingDirectory: str,
            wireguardAddress: str
    ):
        self.id = id
        self.bootstrapAddress = bootstrapAddress
        self.workingDirectory = Path(workingDirectory)
        self.wireguardAddress = wireguardAddress

    def __str__(self):
        return f"{{id: \"{self.id}\", bootstrapAddress: \"{self.bootstrapAddress}\"}}"

    @classmethod
    def from_yaml(cls, constructor, node):
        data = CommentedMap()
        constructor.construct_mapping(node, data, deep=True)
        return cls(**data)


def load_configuration(path: Path) -> Configuration:
    return yaml.load(path)


configuration: Configuration = None
firmware_retriever: FirmwareRetriever = None