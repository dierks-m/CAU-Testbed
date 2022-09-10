import datetime
import re
from pathlib import Path
from typing import List

from tools import request


def format_duration(td):
    """Formats a timedelta instance into a correct ISO 8601 duration string.
    Args:
        td: a datetime.timedelta instance.
    Returns:
        a ISO 8601 duration string.
    """

    s = td.seconds

    ms = td.microseconds
    if ms != 0:  # Round microseconds to milliseconds.
        ms /= 1000000
        ms = round(ms, 3)
        s += ms

    return "P{}DT{}S".format(td.days, s)


def assert_start_end_time(experiment: dict):
    if not "start" in experiment or not "end" in experiment:
        raise RuntimeError("Start or end time not specified.")

    time_pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(?::\\d{2})?"

    if not re.match(time_pattern, experiment["start"]) or not re.match(time_pattern, experiment["end"]):
        print("Time must be given in format YYYY-MM-DDTHH:MM[:SS]")
        raise RuntimeError("Start or end time format incorrect")


def parse_duration(experiment: dict):
    if "duration" not in experiment:
        raise RuntimeError("Duration not specified.")

    duration_str = experiment["duration"]

    match = re.match("(\\d+):(\\d{2}):(\\d{2}):(\\d{2})", duration_str)
    if match:
        return datetime.timedelta(
            days=int(match.group(1)),
            hours=int(match.group(2)),
            minutes=int(match.group(3)),
            seconds=int(match.group(4))
        )

    match = re.match("(\\d+):(\\d{2}):(\\d{2})", duration_str)
    if match:
        return datetime.timedelta(
            hours=int(match.group(1)),
            minutes=int(match.group(2)),
            seconds=int(match.group(3))
        )

    match = re.match("(\\d+):(\\d{2})", duration_str)
    if match:
        return datetime.timedelta(minutes=int(match.group(1)), seconds=int(match.group(2)))

    match = re.match("(\\d+)", duration_str)
    if match:
        return datetime.timedelta(seconds=int(match.group(1)))

    raise RuntimeError("Duration must be given in [[[dd:]hh:]mm:]ss")


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


def create_experiment(experiment: dict, server_address: str, api_key: str):
    response = request.do_request(server_address, "create-experiment", request.RequestType.POST, experiment, api_key)
    print(f'Successfully created experiment {experiment["name"]} (ID {response["id"]}).')

    return response["id"]


def queue_experiment(experiment: dict, server_address: str, api_key: str):
    response = request.do_request(server_address, "queue-experiment", request.RequestType.POST, experiment, api_key)
    print(f'Successfully created experiment {experiment["name"]} (ID {response["id"]}).')
    print(f'The experiment will execute from {datetime.datetime(*response["start"])} to {datetime.datetime(*response["end"])}')

    return response["id"]


def upload_firmware_files(experiment_id: int, firmware_folder: Path, firmware_file_list: list[str], server_address: str, api_key: str):
    for firmware_file in firmware_file_list:
        request.multipart_request(server_address, "upload-firmware", {
            "file": open(firmware_folder.joinpath(firmware_file), 'rb'),
            "experimentId": experiment_id,
            "name": firmware_file
        }, api_key)

    print("Successfully uploaded firmware files.")


def schedule_experiment(experiment_id: int, server_address: str, api_key: str):
    request.do_request(server_address, "schedule-experiment", request.RequestType.POST, {"id": experiment_id}, api_key)
    print("Successfully scheduled experiment.")


class ExperimentHandler:
    def __init__(self, server_address: str, api_key: str, firmware_folder: Path):
        self.firmware_folder = firmware_folder
        self.api_key = api_key
        self.server_address = server_address

    def schedule_experiment(self, experiment: dict):
        firmware_files = extract_firmware_files(experiment)
        assert_start_end_time(experiment)
        assert_firmware_exists(self.firmware_folder, firmware_files)

        experiment_id = create_experiment(experiment, self.server_address, self.api_key)
        upload_firmware_files(experiment_id, self.firmware_folder, firmware_files, self.server_address, self.api_key)
        schedule_experiment(experiment_id, self.server_address, self.api_key)

    def queue_experiment(self, experiment: dict):
        firmware_files = extract_firmware_files(experiment)
        duration = parse_duration(experiment)
        experiment["duration"] = format_duration(duration)  # Make it a compatible format

        assert_firmware_exists(self.firmware_folder, firmware_files)

        experiment_id = queue_experiment(experiment, self.server_address, self.api_key)
        upload_firmware_files(experiment_id, self.firmware_folder, firmware_files, self.server_address, self.api_key)
        schedule_experiment(experiment_id, self.server_address, self.api_key)

