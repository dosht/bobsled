package com.retail.domain.transformations

import fixtures.SparkFixture
import munit.FunSuite
import org.apache.spark.sql.functions._

class CleanTest extends FunSuite with SparkFixture {

  spark.test("testDataIntegrity") { spark =>
    import spark.implicits._
    val inputDF = Seq(
      ("A", Some(1), Some(10.0)),
      ("A", Some(2), Some(20.0)),
      ("B", Some(3), Some(30.0)),
      ("B", Some(4), Some(40.0)),
      ("C", None, None)
    ).toDF("StockCode", "Quantity", "Price")

    val clean = new Clean(inputDF)
    val totalRows = clean.df.count()
    val distinctStockCodes = clean.df.select("StockCode").distinct().count()
    val nullValues = clean.df.filter(col("StockCode").isNull || col("Quantity").isNull || col("Price").isNull).count()

    assertEquals(totalRows, 4L)
    assertEquals(distinctStockCodes, 2L)
    assertEquals(nullValues, 0L)
  }
}
