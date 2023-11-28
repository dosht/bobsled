package com.luxoft.sensors

import com.luxoft.sensors.CatsJob._
import munit.CatsEffectSuite

class CatsJobTest extends CatsEffectSuite {
  test("extract metrics from files") {
    val files = Seq("data/leader-0.csv", "data/leader-1.csv") map Path
    extract(files).map { result =>
      assert(result.nonEmpty)
      assert(result.forall(_.nonEmpty))
    }
  }
  test("test readMeasurement returns sensor id and none if humidity is NaN") {
    val result = readMeasurement(RawLine("s659,NaN"))
    assertEquals(result, Some(SensorId("s659") -> None))
  }
  test("test read Metrics returns None if the raw is invalid") {
    val result = readMeasurement(RawLine("invalid"))
    assertEquals(result, None)
  }
  test("test read Metrics returns a metric") {
    val result = readMeasurement(RawLine("s659,55"))
    result contains (SensorId("s659") -> Some(55))
  }
}
