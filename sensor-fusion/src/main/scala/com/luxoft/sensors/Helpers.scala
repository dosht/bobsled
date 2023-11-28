package com.luxoft.sensors

import scala.jdk.CollectionConverters._
import java.nio.file.{FileSystems, Files}

object Helpers {
  def parseArgs(args: Array[String]): Either[ParseArgsError, Path] =
    if (args.isEmpty) {
      Left(ParseArgsError("Missing 1 positional argument"))
    } else {
      Right(Path(args.head))
    }

  def allFiles(path: Path): Either[PathError, List[Path]] = {
    val dir = FileSystems.getDefault.getPath(path.value)
    if (!Files.exists(dir)) {
      Left(PathError("Directory not found!"))
    } else {
      val files = Files.list(dir)
      Right(files
        .iterator()
        .asScala
        .toList
        .map(_.toString)
        .filter(_.endsWith(".csv"))
        .map(Path.apply))
    }
  }
}
