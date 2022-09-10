#! /usr/bin/env python3
import os
from datetime import datetime
from datetime import timedelta
from enum import Enum
from pathlib import Path

import requests

try:
    from tabulate import tabulate
    tabulate_installed = True
except ImportError as e:
    tabulate_installed = False

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
    STOPPING = ("STOPPING", "Stopping")
    FAILED_TO_RETRIEVE_LOGS = ("FAILED_TO_RETRIEVE_LOGS", "Failed to retrieve all logs")
    FAILED_TO_START = ("FAILED_TO_START", "Failed to start")
    CANCELLED = ("CANCELLED", "Cancelled")
    DONE = ("DONE", "Done")


def get_experiment_list(server_address: str, start_delta: timedelta = timedelta(hours=1), end_delta: timedelta = timedelta(hours=12)):
    start = datetime.now() - start_delta

    end = datetime.now() + end_delta

    return request.do_request(
        server_address, "list-experiments", request.RequestType.GET,
        {"start": str(start).replace(' ', 'T'), "end": str(end).replace(' ', 'T')}
    )


def format_line(item: dict):
    return [
        item["id"],
        item["name"],
        item["status"].display_value,
        str(datetime(*item["start"])),
        str(datetime(*item["end"]))
    ]


def compile_table(*args):
    output_table = []

    for i in range(len(args)):
        if len(output_table) > 0 and len(args[i]) > 0:
            output_table.append([])

        for item in args[i]:
            output_table.append(format_line(item))

    return output_table


def print_experiment_list(experiment_list: list):
    if len(experiment_list) == 0:
        print("No experiments currently scheduled.")
        return

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

    output_table = compile_table(list_created, list_scheduled, list_started, list_done)

    print()

    if tabulate_installed:
        print(tabulate(output_table, headers=["ID", "Name", "Status", "Start", "End"], maxcolwidths=[None, 55], tablefmt='fancy_grid'))
    else:
        print_list(output_table)
    print()


def print_list(experiment_list):
    for item in experiment_list:
        if len(item) > 0:
            print(
                f'{item[1]} (ID {item[0]})\t[{item[2]}]:\t{item[3]}\t->\t{item[4]}'
            )
        else:
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
