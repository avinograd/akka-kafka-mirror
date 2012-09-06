import sbt._
import sbt.Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object AkkaKafkaMirrorBuild extends Build {

  lazy val akkakafkamirror = Project(
    id = "akka-kafka-mirror",
    base = file("."),
    settings = assemblySettings ++ Project.defaultSettings ++ Seq(
      name := "akka-kafka-mirror",
      organization := "com.griddynamics.kafka",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      resolvers ++= Seq(
        "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
        "Local Repository" at "file:///home/avinogradov/.m2/repository"
      ),
      libraryDependencies ++= Seq(
        "com.typesafe.akka" % "akka-actor" % "2.0.1",
        "org.slf4j" % "slf4j-api" % "1.6.4",
        "ch.qos.logback" % "logback-classic" % "1.0.6",
        "commons-logging" % "commons-logging" % "1.1.1",
        "joda-time" % "joda-time" % "1.6",
        "kafka" % "core-kafka-assembly" % "0.7.1"
      )
    )
  )
}
