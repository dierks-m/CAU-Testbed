#! /usr/bin/env python3
import os
import sys
from pathlib import Path

import requests.exceptions

import tools.configuration
import tools.request as request

if len(sys.argv) < 2:
    print(f'Usage: {sys.argv[0]} <experiment-id> [target-directory]')
    exit(1)

experiment_id = int(sys.argv[1])
target_directory = Path(sys.argv[2]) if len(sys.argv) == 3 else Path(os.getcwd()).joinpath("results")
server_address = tools.configuration.get_server_address(Path(os.getcwd()))
api_key = tools.configuration.get_api_key(Path(os.getcwd()))

if server_address is None:
    print("No server_address.txt present in config folder")
    exit(1)

if api_key is None:
    print("No api_key.txt present in config folder")
    exit(1)

try:
    file_name = request.download_file(server_address, "get-results", target_directory, {"id": experiment_id}, api_key)
    print(f'Successfully downloaded \'{file_name}\' to \'{target_directory}\'.')
except requests.exceptions.ConnectionError:
    print("Could not connect to server. Perhaps the address is incorrect?")
except RuntimeError as e:
    print("Could not retrieve results (" + str(e) + ")")
    exit(1)
