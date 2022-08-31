#! /usr/bin/env python3

import os
from pathlib import Path

import requests

import tools.configuration
import tools.request as request

server_address = None


def get_node_status():
    response = request.do_request(server_address, "get-node-status", request.RequestType.GET)

    return response


def print_node_status(node_list: list[dict]):
    for node in node_list:
        print(f'{node["id"]}: {node["status"]}')


server_address = tools.configuration.get_server_address(Path(os.getcwd()))

if server_address is None:
    print("No server_address.txt present in config folder")
    exit(1)

try:
    print_node_status(get_node_status())
except requests.exceptions.ConnectionError:
    print("Could not connect to server. Perhaps the address is incorrect?")