package fixtures

import munit.FunSuite
import org.apache.spark.sql.SparkSession

trait SparkFixture { self: FunSuite =>
  /**
   * Create a spark session and stop it after the test is done
   */
  val spark: FunFixture[SparkSession] = FunFixture[SparkSession](
    setup = { test =>
      SparkSession.builder.appName(test.name).master("local[*]").getOrCreate()
    }, teardown = _.stop()
  )

}
