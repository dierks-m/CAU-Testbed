#! /usr/bin/env python3

import json
import os
import sys
import re

import ruamel.yaml
from pathlib import Path

import tools.configuration
import tools.request as request

yaml = ruamel.yaml.YAML(typ='safe')
server_address = None
api_key = None

def load_configuration(input_file: Path):
    with open(input_file, 'r') as input:
        return yaml.load(input)


def dict_to_json(input: dict):
    return json.dumps(input, separators=(',', ':'))


def assert_start_end_time(experiment: dict):
    if not "start" in experiment or not "end" in experiment:
        raise RuntimeError("Start or end time not specified.")

    time_pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(?::\\d{2})?"

    if not re.match(time_pattern, experiment["start"]) or not re.match(time_pattern, experiment["end"]):
        print("Time must be given in format YYYY-MM-DDTHH:MM[:SS]")
        raise RuntimeError("Start or end time format incorrect")


def assert_firmware_exists(firmware_folder: Path, firmware_list: list[str]):
    if not firmware_folder.is_dir():
        raise RuntimeError("Firmware folder does not exist")

    for firmware_item in firmware_list:
        if not firmware_folder.joinpath(firmware_item).is_file():
            raise RuntimeError(f'Firmware file {firmware_item} does not exist')


def extract_firmware_files(experiment: dict):
    firmware_file_list = {}

    if not "nodes" in experiment:
        raise RuntimeError("No nodes specified in experiment")

    nodes = experiment["nodes"]

    if type(nodes) != list:
        raise RuntimeError("Nodes must be given in list form")

    for node in nodes:
        if not "modules" in node:
            raise RuntimeError(f"No modules specified for module {node['id']}")

        modules = node["modules"]

        if type(modules) != list:
            raise RuntimeError("Modules must be given in list form")

        for module in modules:
            if not "firmware" in module:
                raise RuntimeError(f'Node {node["id"]} does not specify firmwarePath for module {module["id"]}')

            firmware_file_list[module["firmware"]] = True

    return list(firmware_file_list)


def create_experiment(experiment: dict):
    response = request.do_request(server_address, "create-experiment", request.RequestType.POST, experiment, api_key)
    print(f'Successfully created experiment {experiment["name"]} (ID {response["id"]}).')

    return response["id"]


def upload_firmware_files(experiment_id: int, firmware_folder: Path, firmware_file_list: list[str]):
    for firmware_file in firmware_file_list:
        request.multipart_request(server_address, "upload-firmware", {
            "file": open(firmware_folder.joinpath(firmware_file), 'rb'),
            "experimentId": experiment_id,
            "name": firmware_file
        }, api_key)

    print("Successfully uploaded firmware files.")


def schedule_experiment(experiment_id: int):
    request.do_request(server_address, "schedule-experiment", request.RequestType.POST, {"id": experiment_id}, api_key)
    print("Successfully scheduled experiment.")

def print_help():
    print("""
    Example: create-experiment.py sample-experiment.yaml ./firmware
    
    Whereas sample-experiment.yaml is the experiment descriptor file and firmware is the folder which contains all the
    necessary firmware files for the given experiment.
    The names of the necessary files are extracted from the experiment descriptor file and all firmware files
    need to be present in the firmware folder.
    
    Note that experiments must keep a space of at least 5 minutes between each other.
    Refer to list-experiments.py to get an overview of all the scheduled experiments.
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

    experiment_dict = load_configuration(Path(sys.argv[1]))
    assert_start_end_time(experiment_dict)
    firmware_list = extract_firmware_files(experiment_dict)
    assert_firmware_exists(Path(sys.argv[2]), firmware_list)

    experiment_id = create_experiment(experiment_dict)
    upload_firmware_files(experiment_id, Path(sys.argv[2]), firmware_list)
    schedule_experiment(experiment_id)
except RuntimeError as e:
    print(str(e))
    exit(1)
