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
      organization := "com.griddynamics",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      resolvers ++= Seq(
        "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
        "Local Repository" at "file:///home/avinogradov/.m2/repository"
      ),
      libraryDependencies ++= Seq(
        "com.typesafe.akka" % "akka-actor" % "2.0.1",
        "kafka" % "core-kafka-assembly" % "0.7.1"
      )
    )
  )
}