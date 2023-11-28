ThisBuild / organization := "com.retail"
ThisBuild / scalaVersion := "2.13.5"

lazy val root = (project in file(".")).settings(
  name := "online-retail",
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-sql" % "3.3.1",
    "com.crealytics" %% "spark-excel" % "3.3.1_0.18.6-beta1",
    "org.apache.logging.log4j" % "log4j-core" % "2.20.0",
    "org.scalactic" %% "scalactic" % "3.2.15",
    "org.scalatest" %% "scalatest" % "3.2.15" % Test,
    "org.scalacheck" %% "scalacheck" % "1.14.1" % Test,
    "org.scalameta" %% "munit" % "0.7.29" % Test,
    "io.findify" %% "s3mock" % "0.2.6" % "test"
  )
)
