package com.luxoft.sensors

import cats.kernel.Semigroup

final case class Metric(max: Int, min: Int, sum: Int, count: Int)

object Metric {
  type ErrorCount = Int

  implicit val metricSemiGroup: Semigroup[Metric] =
    (x: Metric, y: Metric) => Metric(
      max = Math.max(x.max, y.max),
      min = Math.min(x.min, y.min),
      sum = x.sum + y.sum,
      count = x.count + y.count)


}