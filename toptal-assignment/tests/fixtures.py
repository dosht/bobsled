import pytest
import os
import pandas as pd
from pathlib import Path


cwd = Path(__file__).parent

@pytest.fixture()
def inventory():
    return pd.read_csv(cwd / 'data' / 'test_inventory.csv')


@pytest.fixture()
def supply():
    return pd.read_csv(cwd / 'data' / 'test_supply.csv')


@pytest.fixture()
def sell():
    return pd.read_csv(cwd / 'data' / 'test_sell.csv')


@pytest.fixture()
def combined_data_frame():
    return pd.read_csv(cwd / 'data' / 'combined_df.csv')
