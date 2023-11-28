import pandas as pd
from pyspark.sql import SparkSession
from pyspark.sql.functions import col, lit, when
from pyspark.sql.functions import input_file_name

from applepen.udf import to_state_udf, to_store_udf, to_apple_udf, to_pen_udf


class DataLoader:
    spark: SparkSession
    data_path: str

    def __init__(self, spark: SparkSession, data_path: str):
        self.spark = spark
        self.data_path = data_path

    def load_data_set(self, prefix):
        files = f"{self.data_path}/*{prefix}.csv"
        return self.spark.read \
            .format("csv") \
            .option("header", "true") \
            .load(files) \
            .withColumn("filename", input_file_name()) \
            .withColumn('state', to_state_udf('filename')) \
            .withColumn('store', to_store_udf('filename'))

    def load_inventory(self):
        return self.load_data_set("inventory") \
            .withColumn('sku_num', lit(None)) \
            .withColumn('source', lit('inventory'))

    def load_supply(self):
        return self.load_data_set("supply") \
            .withColumn('sku_num', lit(None)) \
            .withColumn('source', lit('supply'))

    def load_sell(self):
        return self.load_data_set("sell") \
            .withColumn('source', lit('sell')) \
            .withColumn('apple', lit(None)) \
            .withColumn('pen', lit(None))


def unify_data_frames(df):
    return df.select(
            col('source'),
            col('state'),
            col('store'),
            col('date'),
            col('apple'),
            col('pen'),
            col('sku_num'))


def process_store(store_df: pd.DataFrame):
    return store_df
    # TODO: Process data of each store similar to the basic solution with a few modifications due to schema change
    # TODO: For each store, we will save the daily inventory and monthly amount of stolen goods data frames into csv


if __name__ == '__main__':
    spark = SparkSession.builder.config("spark.jars", "/path/to/gcs-connector-hadoop2-latest.jar").getOrCreate()
    data_loader = DataLoader(spark, "gs://applepen-input-1/*.csv")
    inventory = unify_data_frames(data_loader.load_inventory())
    supply = unify_data_frames(data_loader.load_supply())
    sell = unify_data_frames(data_loader.load_sell())
    df = inventory.union(supply).union(sell)
    df = df.withColumn("apple", when(df.source == 'sell', to_apple_udf(df.sku_num)).otherwise(df.apple)) \
        .withColumn("pen", when(df.source == 'sell', to_pen_udf(df.sku_num)).otherwise(df.pen))

    yearly_overview = df.groupby('state', 'store').applyInPandas(process_store, df.schema).show()
    # TODO: Save the yearly overview report into csv

