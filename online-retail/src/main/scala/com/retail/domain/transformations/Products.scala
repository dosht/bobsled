package com.retail.domain.transformations

import com.retail.presentaion.excel.ExcelWritable
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

class Products(clean: Clean) {
  import Products._

  val popularProducts: DataFrame = calculatePopularProducts(clean.df)
}
object Products {
  /**
   * Calculates the top N products summing quantities grouped by StockCode
   * @param df: DataFrame with valid only rows
   * @param limit: Number of top N products
   * @return a new DataFrame with TotalQuantity column
   */
  def calculatePopularProducts(df: DataFrame, limit: Int = 10): DataFrame = df
    .groupBy("StockCode")
    .agg(sum("Quantity").alias("TotalQuantity"))
    .orderBy(desc("TotalQuantity"))
    .limit(limit)

  /**
   * Defines sheet names and data frames to be represented in the output excel
   */
  implicit val toExcel: ExcelWritable[Products] = new ExcelWritable[Products] {
    override def tables(a: Products): Map[String, DataFrame] = Map(
      "'Most Popular Products'!A1" -> a.popularProducts
    )
  }
}
