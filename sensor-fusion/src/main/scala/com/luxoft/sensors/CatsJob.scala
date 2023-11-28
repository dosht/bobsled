package com.luxoft.sensors

import cats.effect.kernel.Resource
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.luxoft.sensors.Helpers._
import com.luxoft.sensors.Metric.ErrorCount

import scala.io.{BufferedSource, Source}

object CatsJob extends IOApp {

  def fileResource(filePath: Path): Resource[IO, BufferedSource] = {
    Resource.make(IO(Source.fromFile(filePath.value)))(release = f => IO(f.close()))
  }

  def extract(files: Seq[Path]): IO[Seq[Map[SensorId, (ErrorCount, Option[Metric])]]] =
    files
      .map(fileResource)
      .map(_.use(fileBuffer =>
        IO(fileBuffer.getLines.next()) *>  // Skip the headers line
        IO(aggregate(fileBuffer.getLines.map(RawLine)))))
      .parSequence

  def readMeasurement(rawLine: RawLine): Option[(SensorId, Option[Int])] = {
    val parts = rawLine.value.split(",")
    if (parts.size != 2) {
      None
    } else {
      val Array(sensor, metric) = parts
      if (!metric.forall(Character.isDigit)) {
        (SensorId(sensor), None).some
      } else {
        (SensorId(sensor), Integer.parseInt(metric).some).some
      }
    }
  }

  def aggregate(rawLines: Iterator[RawLine]): Map[SensorId, (ErrorCount, Option[Metric])] =
    rawLines.map(readMeasurement).flatMap(metricOpt =>
      metricOpt.map {
        case (sensorId, humidityOpt) =>
          sensorId -> humidityOpt.fold[(ErrorCount, Option[Metric])](1 -> None)(humidity =>
          0 -> Metric(humidity, humidity, humidity, 1).some)
      }
    ).toMap

  final def run(args: List[String]): IO[ExitCode] = {
    val report: IO[String] = for {
      dir <- IO.fromEither(parseArgs(args.toArray))
      files <- IO.fromEither(allFiles(dir))
      partitions <- extract(files)
    } yield {
      val reducedAggregation = partitions.reduce(_ |+| _)
      Report(files.size, reducedAggregation).report
    }
    report.flatMap(IO.println).as(ExitCode.Success)
  }
}
