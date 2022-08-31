import requests
from enum import Enum
import urllib


class RequestType(Enum):
    GET = 1
    POST = 2


def do_request(server_address: str, resource: str, request_type: RequestType, json_data: dict = None, auth: str = None):
    target = urllib.parse.urljoin(server_address, resource)

    if auth is not None:
        auth = (auth, '')

    response = None

    if request_type == RequestType.GET:
        response = requests.get(target, json=json_data, auth=auth)
    elif request_type == RequestType.POST:
        response = requests.post(target, json=json_data, auth=auth)

    json_content = {}

    try:
        json_content = response.json()
    except:
        pass

    if response.status_code != requests.codes["ok"]:
        if "error" in json_content:
            raise RuntimeError(json_content["error"])
        else:
            raise RuntimeError("Error: " + str(response) + ' ' + str(response.content))

    return json_content


def multipart_request(server_address: str, resource: str, files: dict, auth: str):
    target = urllib.parse.urljoin(server_address, resource)

    response = requests.post(target, files=files, auth=(auth, ''))
    json_content = {}

    try:
        json_content = response.json()
    except:
        pass

    if response.status_code != requests.codes["ok"]:
        if "error" in json_content:
            raise RuntimeError(json_content["error"])
        else:
            raise RuntimeError("Error: " + str(response))

    return json_content

    return json_content

