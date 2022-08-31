from pathlib import Path

def get_config_file_contents_or_none(working_dir: Path, file_name: str):
    config_dir = working_dir.joinpath("config")

    if not config_dir.is_dir():
        return None

    file_path = config_dir.joinpath(file_name)

    if not file_path.is_file():
        return None

    with open(file_path, 'r') as file:
        return file.read().replace('\n', '')

def get_server_address(working_dir: Path):
    return get_config_file_contents_or_none(working_dir, "server_address.txt")

def get_api_key(working_dir: Path):
    return get_config_file_contents_or_none(working_dir, "api_key.txt")