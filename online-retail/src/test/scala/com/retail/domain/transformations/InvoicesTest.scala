package com.retail.domain.transformations

import fixtures.SparkFixture
import munit.FunSuite

class InvoicesTest extends FunSuite with SparkFixture {
  spark.test("testTotalRevenue") { spark =>
    import spark.implicits._

    val inputDF = Seq(
      ("A", 1, 10.0),
      ("A", 2, 20.0),
      ("B", 3, 30.0),
      ("B", 2, 40.0)
    ).toDF("StockCode", "Quantity", "Price")

    val outputDF = Seq(
      ("A", 50.0),
      ("B", 170.0)
    ).toDF("StockCode", "TotalRevenue")

    val revenueDF = Revenue.calculateRevenue(inputDF)
    val totalRevenueDF = Revenue.calculateTotalRevenue(revenueDF)

    assertEquals(totalRevenueDF.collect().toList, outputDF.collect().toList)
  }
}
