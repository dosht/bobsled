package com.retail.domain.model

import org.apache.spark.sql.types.{DateType, DoubleType, IntegerType, StringType, StructField, StructType}

object OnlineRetail {
  /*
   * The schema of the online retail row.
   * We can take this one step further by create case classes and convert the DataFrame into DataSets
   */
  val schema: StructType = new StructType()
      .add("Invoice", IntegerType, nullable = false)
      .add("StockCode", StringType, nullable = false)
      .add("Description", StringType, nullable = true)
      .add("Quantity", IntegerType, nullable = false)
      .add("InvoiceDate", DateType, nullable = false)
      .add("Price", DoubleType, nullable = false)
      .add("Customer ID", IntegerType, nullable = false)
      .add("Country", StringType, nullable = true
  )
}
