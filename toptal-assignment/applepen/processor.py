from pathlib import Path
from tqdm import tqdm
import pandas as pd

from applepen.utils import parse_file_name


def combine_data_frames(inventory: pd.DataFrame, supply: pd.DataFrame, sell: pd.DataFrame) -> pd.DataFrame:
    products = {
        'ap': 'apple',
        'pe': 'pen'
    }

    def to_product(sku):
        return products[sku.split("-")[2]]

    sell['product'] = sell['sku_num'].apply(to_product)

    pivot = sell \
        .groupby(['date', 'product'])['sku_num'] \
        .count() \
        .reset_index() \
        .rename(columns={'sku_num': 'sales'}) \
        .pivot(index='date', columns='product')

    df = pd.concat([
        pd.merge(pivot.sales, inventory, on='date', how='left', suffixes=('_sold', '_inventory')),
        pd.merge(pivot.sales, supply, on='date', how='left', suffixes=('_sold', '_supply'))[
            ['apple_supply', 'pen_supply']]
    ], axis=1)

    df['expected_pen_inventory'] = (df.pen_supply.fillna(0).cumsum() - df.pen_sold.fillna(0).cumsum()).astype(int)
    df['expected_apple_inventory'] = (df.apple_supply.fillna(0).cumsum() - df.apple_sold.fillna(0).cumsum()).astype(int)
    return df


def calculate_daily_inventory(df: pd.DataFrame) -> pd.DataFrame:
    return df[['date', 'expected_apple_inventory', 'expected_pen_inventory']] \
        .rename(columns={'expected_apple_inventory': 'apple', 'expected_pen_inventory': 'pen'})


def calculate_stolen_items(df: pd.DataFrame) -> pd.DataFrame:
    compare_df = df[
        ['date', 'apple_inventory', 'pen_inventory', 'expected_pen_inventory', 'expected_apple_inventory']].dropna()

    compare_df['apple_stolen'] = (compare_df['expected_apple_inventory'] - compare_df['apple_inventory']).astype(int)
    compare_df['pen_stolen'] = (compare_df['expected_pen_inventory'] - compare_df['pen_inventory']).astype(int)

    return compare_df[['date', 'apple_stolen', 'pen_stolen']] \
        .rename(columns={"apple_stolen": 'apple', 'pen_stolen': 'pen'})


def create_overview(state: str, df: pd.DataFrame, stolen_df: pd.DataFrame) -> pd.DataFrame:
    df['year'] = pd.to_datetime(df['date']).dt.year
    stolen_df['year'] = pd.to_datetime(stolen_df['date']).dt.year
    df.groupby('year')[['apple_sold', 'pen_sold']].sum()

    overview_df = pd.concat([
        stolen_df
        .rename(columns={'apple': 'apple_stolen', 'pen': 'pen_stolen'})
        .groupby('year')[['apple_stolen', 'pen_stolen']].sum(),
        df.groupby('year')[['apple_sold', 'pen_sold']].sum()
    ], axis=1)

    overview_df['state'] = state
    return overview_df[['state', 'apple_sold', 'apple_stolen', 'pen_sold', 'pen_stolen']]


def process_store(data_path: Path, state: str, store: str, output_path: Path):
    inventory = pd.read_csv(data_path / f"{state}-{store}-inventory.csv")
    supply = pd.read_csv(data_path / f"{state}-{store}-supply.csv")
    sell = pd.read_csv(data_path / f"{state}-{store}-sell.csv")

    df = combine_data_frames(inventory, supply, sell)

    # Daily inventory for each store
    calculate_daily_inventory(df).to_csv(output_path / f'{state}-{store}-daily_inventory.csv', index=False)

    # Monthly amount of stolen goods for each store.
    stolen_df = calculate_stolen_items(df)
    stolen_df.to_csv(output_path / f"{state}-{store}-monthly_stolen.csv", index=False)

    # High level overview with sales volume and amount of stolen goods by state and year.
    return create_overview(state, df, stolen_df)


def process_all_stores(data_path: Path, output_path: Path):
    files = data_path.iterdir()
    overview_dfs = []
    for state, store in tqdm(set(parse_file_name(f.name) for f in files)):
        overview_df = process_store(data_path, state, store, output_path)
        overview_dfs.append(overview_df)

    pd.concat(overview_dfs) \
        .groupby(['year', 'state']) \
        .sum() \
        .to_csv(output_path / "yearly_overview.csv")