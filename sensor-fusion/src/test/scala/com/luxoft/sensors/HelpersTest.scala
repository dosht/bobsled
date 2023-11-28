package com.luxoft.sensors

import munit.FunSuite

class HelpersTest extends FunSuite {
    test("test parseArgs with empty arguments") {
      val result = Helpers.parseArgs(Array.empty)
      assertEquals(result, Left(ParseArgsError("Missing 1 positional argument")))
    }
    test("test parseArgs with empty arguments") {
      val result = Helpers.parseArgs(Array("dir_path"))
      assertEquals(result, Right(Path("dir_path")))
    }
    test("test allFiles returns error if the directory doesn't exist") {
      val result = Helpers.allFiles(Path("invalid_path"))
      assertEquals(result, Left(PathError("Directory not found!")))
    }
    test("test allFiles returns list of all files in directory") {
      val result = Helpers.allFiles(Path("data"))
      assert(result.isRight)
      assert(result.toOption.get.nonEmpty)
    }
    test("test allFiles returns only csv files") {
      val result = Helpers.allFiles(Path("data"))
      assert(result.toOption.get.forall(_.value.endsWith(".csv")))
    }
}
