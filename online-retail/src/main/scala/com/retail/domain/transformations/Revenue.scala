package com.retail.domain.transformations

import com.retail.presentaion.excel.ExcelWritable
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.storage.StorageLevel

class Revenue(clean: Clean) {

  import Revenue._

  val revenue: DataFrame = calculateRevenue(clean.df)
    .persist(StorageLevel.MEMORY_AND_DISK)

  val totalRevenue: DataFrame = calculateTotalRevenue(revenue)

  val averageRevenueByCategory: DataFrame = calculateAverageRevenueByCategory(revenue)

  val monthlyRevenue: DataFrame = calculateMonthlyRevenue(Invoices.calculateMonthlyInvoices(revenue))
}

object Revenue {
  /**
   * Adds revenue column by multiplying quantities by price
   * @param df: DataFrame with valid only rows
   * @return a new DataFrame with Revenue column
   */
  def calculateRevenue(df: DataFrame): DataFrame = df
    .withColumn("Revenue", col("Quantity") * col("Price"))

  /**
   * Sums Revenue grouped by StockCode
   * @param df: DataFrame with Revenue column
   * @return a new DataFrame with TotalRevenue column
   */
  def calculateTotalRevenue(df: DataFrame): DataFrame = df
    .groupBy("StockCode")
    .agg(sum("Revenue").alias("TotalRevenue"))

  /**
   * Calculates average revenue per category where category is the first 3 letters of StockCode
   * @param df: DataFrame with Revenue column
   * @return a new DataFrame with AverageRevenueByCategory
   */
  def calculateAverageRevenueByCategory(df: DataFrame): DataFrame = df
    .withColumn("Category", substring(col("StockCode"), 0, 3))
    .groupBy("Category")
    .agg(avg("Revenue").alias("AverageRevenueByCategory"))

  /**
   * Calculates total monthly revenue
   * @param df: DataFrame with Revenue and InvoiceMonth columns
   * @return a new DataFrame with MonthlyRevenue column
   */
  def calculateMonthlyRevenue(df: DataFrame): DataFrame = df
    .groupBy("InvoiceMonth")
    .agg(sum("Revenue").alias("MonthlyRevenue"))

  /**
   * Defines sheet names and data frames to be represented in the output excel
   */
  implicit val toExcel: ExcelWritable[Revenue] = new ExcelWritable[Revenue]() {
    override def tables(a: Revenue): Map[String, DataFrame] = Map(
      "'Total Revenue'!A1" -> a.totalRevenue,
      "'Average Revenue By Category'!A1" -> a.averageRevenueByCategory,
      "'Monthly Revenue'!A1" -> a.monthlyRevenue
    )
  }


}
