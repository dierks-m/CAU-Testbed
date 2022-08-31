#! /usr/bin/env python3 

import sys
import requests

def gen_api_key(server_address:str, user_name:str):
    response = requests.post(server_address + "/create-user", json={"name": user_name})
    json_content = response.json()

    if response.status_code != requests.codes["ok"]:
        print("Encountered error")

        if json_content["error"] is not None:
            print(json_content["error"])
        else:
            print(json_content)

    return json_content["apiKey"]


if len(sys.argv) < 3:
    print(f"Usage: {sys.argv[0]} <server-address> <user-name>")
    print()
    print("The API key will be written to standard output. This can be used as follows:")
    print(f'"{sys.argv[0]} > config/api_key.txt"')
    exit(1)

print(gen_api_key(sys.argv[1], sys.argv[2]))