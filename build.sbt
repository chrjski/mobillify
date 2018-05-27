name := "Mobillis2"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.sparkjava" % "spark-core" % "2.7.2",
  "com.sparkjava" % "spark-template-velocity" % "2.7.1",

  "org.slf4j" % "slf4j-simple" % "1.7.24",
  "org.slf4j" % "slf4j-api" % "1.7.25"
)