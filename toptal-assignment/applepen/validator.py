import pandas as pd
from pathlib import Path

from applepen.utils import parse_file_name


def validate(output_dir: Path):
    stolen_dfs = []
    daily_inventory_dfs = []
    yearly_overview = pd.read_csv(output_dir / 'yearly_overview.csv', keep_default_na=False)
    for path in output_dir.iterdir():
        if "stolen" in path.name:
            df = read_store_dataframe(path)
            stolen_dfs.append(df)

        if "daily_inventory" in path.name:
            df = read_store_dataframe(path)
            daily_inventory_dfs.append(df)

    stolen_df = pd.concat(stolen_dfs)
    daily_inventory_df = pd.concat(daily_inventory_dfs)
    print("Validating solen items data for nulls, negative values, and duplicates")
    if assert_no_check_nulls_and_negatives(stolen_df, columns=['apple', 'pen']) and \
            assert_no_duplicates(stolen_df, columns=['date', 'state', 'store']):
        print("Solen items data is valid\n")

    else:
        print("Solen items data has unexpected values\n")

    print("Validating daily inventory data for nulls, negative values, and duplicates")
    if assert_no_check_nulls_and_negatives(daily_inventory_df, columns=['apple', 'pen']) and \
            assert_no_duplicates(daily_inventory_df, columns=['date', 'state', 'store']):
        print("Daily inventory data is valid\n")

    else:
        print("Daily inventory data has unexpected values\n")

    print("Validating yearly overview data for nulls, negative values, and duplicates")
    if assert_no_check_nulls_and_negatives(yearly_overview, columns=['apple_sold', 'pen_sold', 'apple_stolen', 'pen_stolen']) and \
            assert_no_duplicates(yearly_overview, columns=['year', 'state']):
        print("yearly overview data is valid\n")

    else:
        print("yearly overview data has unexpected values\n")


def read_store_dataframe(path: Path):
    state, store = parse_file_name(path.name)
    df = pd.read_csv(path)
    df['state'] = state
    df['store'] = store
    return df


def assert_no_check_nulls_and_negatives(df: pd.DataFrame, columns: list[str]) -> bool:
    result = True

    has_null_values = df.isnull().values.any()

    # Check for negative values in 'apple' and 'pen' columns

    has_negative_values = any(map(lambda a: a.any(), (df[col] < 0 for col in columns)))

    if has_null_values:
        result = False
        print("The DataFrame contains null values.")

    else:
        print("The DataFrame does not contain null values.")

    if has_negative_values:
        result = False
        print(f"One of {columns} columns contain negative values.")

    else:
        print(f"The {columns} columns do not contain negative values.")

    return result


def assert_no_duplicates(df: pd.DataFrame, columns: list[str]) -> bool:
    duplicates = df.duplicated(subset=columns)

    if duplicates.any():
        print("The DataFrame contains duplicates of year and state pair.")
        return False

    else:
        print("The DataFrame does not contain duplicates of year and state pair.")
        return True
