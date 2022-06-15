from ruamel.yaml import YAML, yaml_object
from pathlib import Path

yaml = YAML()


@yaml_object(yaml)
class Configuration:
    def __init__(self, id: str, bootstrapAddress: str):
        self.id = id
        self.bootstrapAddress = bootstrapAddress

    def __str__(self):
        return f"{{id: \"{self.id}\", bootstrapAddress: \"{self.bootstrapAddress}\"}}"


def load_configuration(path: Path) -> Configuration:
    return yaml.load(path)
