package com.luxoft.sensors

import munit.{CatsEffectSuite, FunSuite}
import cats.implicits._
import cats.syntax._

class MetricTest extends CatsEffectSuite {

  test("Metric can combine") {
    val x = Map(SensorId("s1") -> Metric(3, 1, 5, 5))
    val y = Map(SensorId("s2") -> Metric(4, 2, 5, 5))
    val combined = Map(SensorId("s1") -> Metric(3, 1, 5, 5), SensorId("s2") -> Metric(4, 2, 5, 5))
    assertEquals(x |+| y, combined)
  }
  test("Metric Map can combine with the same key") {
    val x = Map(SensorId("s1") -> Metric(3, 1, 5, 5))
    val y = Map(SensorId("s1") -> Metric(4, 2, 5, 5))
    val combined = Map(SensorId("s1") -> Metric(4, 1, 10, 10))
    assertEquals(x |+| y, combined)
  }
  test("Failed metric can combine left counts") {
    val x: (Int, Option[Metric]) = (0, Metric(1, 1, 1, 1).some)
    val y: (Int, Option[Metric]) = (1, None)
    val z: (Int, Option[Metric]) = (1, None)
    val combined = (2, Metric(1, 1, 1, 1).some)
    assertEquals(x |+|  y |+| z, combined)
  }

}
