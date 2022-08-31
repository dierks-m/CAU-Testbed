#! /usr/bin/env python3
import os
from datetime import datetime
from datetime import timedelta
from pathlib import Path

import requests

import tools.request as request
from tools.configuration import get_server_address


def get_experiment_list(server_address: str, start: datetime = None, end: datetime = None):
    if start is None:
        start = datetime.now() - timedelta(hours=1)

    if end is None:
        end = datetime.now() + timedelta(hours=12)

    return request.do_request(
        server_address, "list-experiments", request.RequestType.GET,
        {"start": str(start).replace(' ', 'T'), "end": str(end).replace(' ', 'T')}
    )


def print_experiment_list(experiment_list: list):
    if len(experiment_list) == 0:
        print("No experiments scheduled.")

    for experiment in experiment_list:
        print(f'{experiment["name"]} (ID {experiment["id"]}):\t{datetime(*experiment["start"])}\t->\t{datetime(*experiment["end"])}')


server_address = get_server_address(Path(os.getcwd()))
config_server_address = get_server_address(Path(os.getcwd()))

if config_server_address is None:
    print("No server_address.txt given in config folder")
    exit(1)

try:
    print_experiment_list(get_experiment_list(server_address))
except requests.exceptions.ConnectionError:
    print("Could not connect to server. Perhaps the address is incorrect?")
