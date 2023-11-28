package com.retail.presentaion.excel

import org.apache.spark.sql.DataFrame

class ExcelWriter(fileName: String) {
  /**
   * Execute the Spark action that writes a result into Excel file
   * @param result: a transformation result that will be written to Excel
   * @tparam A: The type of the transformation param
   */
  def write[A : ExcelWritable](result: A): Unit =
    implicitly[ExcelWritable[A]].tables(result).foreach {
      case (sheetName, df) =>  df.write
        .format("com.crealytics.spark.excel")
        .option("dataAddress", sheetName)
        .option("header", "true")
        .option("dateFormat", "yy-mmm-d")
        .option("timestampFormat", "mm-dd-yyyy hh:mm:ss")
        .mode("append")
        .save(fileName)
    }
}

/**
 * Type class that specifies the sheets names and data frames that will be written into Excel
 * @tparam A The type of the transformation class
 */
trait ExcelWritable[A] {
  def tables(a: A): Map[String, DataFrame]
}
