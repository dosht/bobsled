package com.luxoft.sensors

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.catalyst.dsl.expressions.DslSymbol
import org.apache.spark.sql.functions.{array, col, exists}
import org.apache.spark.sql.types.{DoubleType, IntegerType}
import org.apache.log4j.Logger
import org.apache.log4j.Level


object SparkJob extends App {
  val app = for {
    dir <- Helpers.parseArgs(args)
    files <- Helpers.allFiles(dir)
  } yield {
    Logger.getLogger("root").setLevel(Level.OFF)
    val spark: SparkSession = SparkSession.builder.appName("Simple Application").master("local[*]").getOrCreate()
    val df = spark.read.format("csv").option("header", "true").load(dir.value).withColumn("humidity", col("humidity").cast(IntegerType))
    val metrics = df.groupBy("sensor-id").agg("humidity" -> "min", "humidity" -> "avg", "humidity" -> "max").collect()

    df.createOrReplaceTempView("Metrics")
    val Row(processMeasurements, failedMeasurements) = spark.sql("SELECT COUNT(humidity) success, count(*) - count(humidity) failed FROM metrics").collect().head

    val numFiles = files.size

    println(
      s"""
       |Num of Processed files: $numFiles
       |Num of processed measurements: $processMeasurements
       |Num of failed measurements: $failedMeasurements
       |
       |sensor-id,min,avg,max
       |${metrics.map { case Row(sensorId, min, avg, max) => s"$sensorId,$min,$avg,$max" }.mkString("\n")}
       |""".stripMargin)

    print("sensor-id,min,avg,max")
    metrics foreach (_.mkString(","))
  }
  app.left.foreach {
    case ParseArgsError(message) =>
      println(message)
      System.exit(1)
    case PathError(message) =>
      println(message)
      System.exit(2)
  }
}
