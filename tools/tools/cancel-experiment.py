#! /usr/bin/env python3
import os
import sys
from pathlib import Path

import requests.exceptions

import tools.configuration
import tools.request as request

if len(sys.argv) != 2:
    print(f'Usage: {sys.argv[0]} <experiment-id>')
    exit(1)

experiment_id = int(sys.argv[1])
server_address = tools.configuration.get_server_address(Path(os.getcwd()))
api_key = tools.configuration.get_api_key(Path(os.getcwd()))

if server_address is None:
    print("No server_address.txt present in config folder")
    exit(1)

if api_key is None:
    print("No api_key.txt present in config folder")
    exit(1)

try:
    response = request.do_request(server_address, "cancel-experiment", request.RequestType.POST, {"id": experiment_id}, api_key)
    print(f'Successfully canceled experiment \'{response["name"]}\'.')
except requests.exceptions.ConnectionError:
    print("Could not connect to server. Perhaps the address is incorrect?")
except RuntimeError as e:
    print("Cannot cancel experiment (" + str(e) + ")")
    exit(1)
