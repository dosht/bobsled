from pyspark.sql.types import IntegerType
from pyspark.sql.functions import udf


def to_state(filename: str) -> str:
    return filename.split("/")[-1][0:5].split("-")[0]


to_state_udf = udf(to_state)


def to_store(filename: str) -> str:
    return filename.split("/")[-1][0:5].split("-")[1]


to_store_udf = udf(to_store)


def to_data_source(filename: str) -> str:
    return filename.split("/")[-1][5:].split("-")[1].split('.')[0]


to_data_source_udf = udf(to_data_source)

products = {
    'ap': 'apple',
    'pe': 'pen'
}


def to_pen(sku_num):
    return 1 if products[sku_num.split("-")[2]] == 'pen' else 0


to_pen_udf = udf(to_pen, IntegerType())


def to_apple(sku_num):
    return 1 if products[sku_num.split("-")[2]] == 'apple' else 0


to_apple_udf = udf(to_apple, IntegerType())
