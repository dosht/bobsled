package com.retail

import com.retail.domain.model.OnlineRetail
import com.retail.domain.transformations.{Clean, Invoices, Products, Revenue}
import com.retail.presentaion.excel.ExcelWriter
import com.retail.read.excel._
import org.apache.spark.sql.SparkSession

object OnlineRetailApp extends App {
  /*
  Creating the Spark session.
  To deploy this to production, this should be based on config to specify parameters such as SPARK_MASTER, NUM_WORKERS, ..etc
  Based on SPARK_MASTER, additional parameters will be required. e.g. k8s will require SPARK_IMAGE, DRIVER_HOST, DRIVER_PORT, ..etc
  The config file should provide default values and those values can be overridden by environment variables
  */
  val spark: SparkSession = SparkSession.builder
    .appName("Online Retail Application")
    .master("local[*]")
    .getOrCreate()

  // In production, this should be read from config or command line arguments
  val fileName = "online_retail_II.xlsx"

  // Extracting records from the excel sheets into data frames
  private val sheet1 = spark.read.excel(fileName, "0!A1", OnlineRetail.schema)
  private val sheet2 = spark.read.excel(fileName, "1!A1", OnlineRetail.schema)

  // Transform the data frames into the final result
  private val clean = new Clean(sheet1 union sheet2)
  private val revenue = new Revenue(clean)
  private val products = new Products(clean)

  // Load the result into the another excel sheet
  private val excelWriter = new ExcelWriter("online_retail_II_report.xlsx")
  excelWriter.write(revenue)
  excelWriter.write(products)

}
