from applepen.processor import process_all_stores
from applepen.validator import validate
from pathlib import Path
import click


@click.command()
@click.option('--input', '-i', required=True, help='Input path.', type=click.Path(exists=True))
@click.option('--output', '-o', required=True, help='Output path.', type=click.Path(exists=True))
@click.option('--validate-only', is_flag=True, help='Perform validation only.')
@click.option('--process-only', is_flag=True, help='Perform processing only.')
def main(input: Path, output: Path, validate_only: bool, process_only: bool):
    input_path = Path(input)
    output_path = Path(output)

    if process_only:
        process_all_stores(input_path)
    elif validate_only:
        validate(Path(output_path))
    else:
        process_all_stores(input_path)
        validate(Path(output_path))
