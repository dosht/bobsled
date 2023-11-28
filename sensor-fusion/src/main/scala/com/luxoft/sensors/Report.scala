package com.luxoft.sensors

import com.luxoft.sensors.Metric.ErrorCount

final case class Report(numFiles: Int, aggregatedMeasurements: Map[SensorId, (ErrorCount, Option[Metric])]) {
  lazy val report: String = {
    s"""
       |Num of Processed files: $numFiles
       |Num of processed measurements: ${aggregatedMeasurements.values.map(_._2.fold(0)(_.count)).sum}
       |Num of failed measurements: ${aggregatedMeasurements.values.map { case (err, _) => err }.sum}
       |
       |sensor-id,min,avg,max
       |${
      aggregatedMeasurements.map {
        case (sensor, (_, Some(Metric(min, max, sum, count)))) =>
          val avg: Int = sum / count
          s"${sensor.value},$min,$avg,$max"
        case (sensor, (_, None)) =>
          s"${sensor.value},NaN"
      }.mkString("\n")
    }
       |""".stripMargin
  }
}
