#! /usr/bin/env python3
import os
from datetime import datetime
from datetime import timedelta
from enum import Enum
from pathlib import Path

import requests

import tools.request as request
from tools.configuration import get_server_address


class ExperimentStatus(str, Enum):
    display_value: str

    def __new__(cls, name: str, display_value: str):
        obj = str.__new__(cls, name)
        obj._value_ = name

        obj.display_value = display_value
        return obj

    CREATED = ("CREATED", "Created")
    SCHEDULED = ("SCHEDULED", "Scheduled")
    STARTED = ("STARTED", "Started")
    FAILED_TO_RETRIEVE_LOGS = ("FAILED_TO_RETRIEVE_LOGS", "Failed to retrieve all logs")
    CANCELLED = ("CANCELLED", "Cancelled")
    DONE = ("DONE", "Done")


def get_experiment_list(server_address: str, start_delta: timedelta = timedelta(hours=1), end_delta: timedelta = timedelta(hours=12)):
    start = datetime.now() - start_delta

    end = datetime.now() + end_delta

    return request.do_request(
        server_address, "list-experiments", request.RequestType.GET,
        {"start": str(start).replace(' ', 'T'), "end": str(end).replace(' ', 'T')}
    )


def print_experiment_list(experiment_list: list):
    if len(experiment_list) == 0:
        print("No experiments currently scheduled.")

    if len(experiment_list) > 1:
        longest_name = max(*map(lambda x: len(x["name"]), experiment_list))
        for x in experiment_list:
            x["name"] = x["name"].ljust(longest_name)

    list_created = []
    list_scheduled = []
    list_started = []
    list_done = []

    for experiment in experiment_list:
        try:
            status = ExperimentStatus(experiment["status"])
            experiment["status"] = status

            if status == ExperimentStatus.CREATED:
                list_created.append(experiment)
            elif status == ExperimentStatus.SCHEDULED:
                list_scheduled.append(experiment)
            elif status == ExperimentStatus.STARTED:
                list_started.append(experiment)
            else:
                list_done.append(experiment)
        except:
            pass

    print_list(list_created)
    print_list(list_scheduled)
    print_list(list_started)
    print_list(list_done, True)


def print_list(experiment_list, is_last = False):
    for experiment in experiment_list:
        print(
            f'{experiment["name"]} (ID {experiment["id"]})\t[{experiment["status"].display_value}]:\t{datetime(*experiment["start"])}\t->\t{datetime(*experiment["end"])}'
        )

    if len(experiment_list) > 0 and not is_last:
        print()


server_address = get_server_address(Path(os.getcwd()))
config_server_address = get_server_address(Path(os.getcwd()))

if config_server_address is None:
    print("No server_address.txt given in config folder")
    exit(1)

try:
    print_experiment_list(get_experiment_list(server_address))
except requests.exceptions.ConnectionError:
    print("Could not connect to server. Perhaps the address is incorrect?")
