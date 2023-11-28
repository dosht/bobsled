package com.retail.domain.transformations

import com.retail.domain.transformations.Clean.dropMissingAndInvalidValues
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

object Invoices {
  /**
   * Adds another column that specify the month by dropping the day component from InvoiceDate column
   * @param df: DataFrame with valid only rows
   * @return a new DataFrame with InvoiceDate column
   */
  def calculateMonthlyInvoices(df: DataFrame): DataFrame = df
    .withColumn("InvoiceMonth", date_format(col("InvoiceDate"), "yyyy-MM"))
}
