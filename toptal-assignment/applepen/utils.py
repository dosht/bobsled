def parse_file_name(filename: str) -> (str, str):
    """Returns - a tuple of (State, Store)"""
    return tuple(filename[0:5].split("-"))
