package com.retail.domain.transformations

import org.apache.spark.sql.DataFrame
import org.apache.spark.storage.StorageLevel

class Clean(input: DataFrame) {
  val df: DataFrame = Clean
    .dropMissingAndInvalidValues(input)
    .persist(StorageLevel.MEMORY_AND_DISK)
}

object Clean {
  /**
   * Find any record with null values and drop them. That should include also records that don't match the schema
   * @param df: DataFrame as is with out cleaning
   * @return a new DataFrame with valid only rows
   */
  def dropMissingAndInvalidValues(df: DataFrame): DataFrame = df.na.drop()
}
