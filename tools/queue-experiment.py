#! /usr/bin/env python3
import datetime
import os
import re
import sys
from pathlib import Path

import ruamel.yaml

import tools.configuration
from tools.experiment import ExperimentHandler
yaml = ruamel.yaml.YAML(typ='safe')
server_address = None
api_key = None


def load_configuration(input_file: Path):
    with open(input_file, 'r') as input:
        return yaml.load(input)


def print_help():
    print("""
    Example: queue-experiment.py experiment-queued.yaml ./firmware

    Whereas queued.yaml is the experiment descriptor file and firmware is the folder which contains all the
    necessary firmware files for the given experiment.
    The names of the necessary files are extracted from the experiment descriptor file and all firmware files
    need to be present in the firmware folder.
    """)


if len(sys.argv) < 3:
    print(f'Usage: {sys.argv[0]} <experiment-file> <path-to-firmware-folder>')
    print_help()
    exit(1)

try:
    server_address = tools.configuration.get_server_address(Path(os.getcwd()))
    api_key = tools.configuration.get_api_key(Path(os.getcwd()))

    if server_address is None:
        print("No server_address.txt given in config folder")
        exit(1)

    if api_key is None:
        print("No API key given in api_key.txt in config folder")
        exit(1)

    handler = ExperimentHandler(server_address, api_key, Path(sys.argv[2]))
    handler.queue_experiment(load_configuration(Path(sys.argv[1])))
except RuntimeError as e:
    print(str(e))
    exit(1)