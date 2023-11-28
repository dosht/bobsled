package com.luxoft

package object sensors {
  final case class Path(value: String) extends AnyVal

  final case class RawLine(value: String) extends AnyVal

  final case class SensorId(value: String) extends AnyVal

  sealed trait Error extends Throwable {
    def message: String
  }

  case class ParseArgsError(message: String) extends Error

  case class PathError(message: String) extends Error

  case class ReadError(message: String) extends Error

  case class SensorError(message: String, sensorId: Option[SensorId] = None) extends Error
}
