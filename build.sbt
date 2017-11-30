import ProjectDependencies._
import ProjectSettings._
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

name := "cat4s"

version in ThisBuild := "1.0.0-SNAPSHOT"

organization in ThisBuild := "cat4s"

scalaVersion in ThisBuild := "2.11.11"

javacOptions in ThisBuild ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

externalResolvers in ThisBuild := Resolver.withDefaultResolvers(Seq.empty, mavenCentral = true)

isSnapshot in ThisBuild := true

//fork in ThisBuild := true

lazy val aggregateProjects: Seq[ProjectReference] = Seq(
  aspectj,
  core,
  osPlugin,
  akkaPlugin,
  jdbcPlugin,
  flumeReporter,
  fluentdReporter,
  logstashReporter,
  consoleReporter
)

lazy val aspectj = (project in file("cat4s-aspectj"))
  .settings(name := "cat4s-aspectj")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(Disruptor))
  .settings(libraryDependencies ++= Seq(LogbackCore))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val core = (project in file("cat4s-core"))
  .settings(name := "cat4s-core")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(Disruptor))
  .settings(libraryDependencies ++= Seq(LogbackCore))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val osPlugin = (project in file("cat4s-plugin-os"))
  .settings(name := "cat4s-plugin-os")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(LogbackClassic))
  .settings(libraryDependencies ++= Seq(Json4sJackson, Json4sExt))
  .settings(libraryDependencies ++= Seq(Flume))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .dependsOn(core)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val akkaPlugin = (project in file("cat4s-plugin-akka"))
  .settings(name := "cat4s-plugin-akka")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(LogbackClassic))
  .settings(libraryDependencies ++= Seq(Json4sJackson, Json4sExt))
  .settings(libraryDependencies ++= Seq(Flume))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .dependsOn(core)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val jdbcPlugin = (project in file("cat4s-plugin-jdbc"))
  .settings(name := "cat4s-plugin-jdbc")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(LogbackClassic))
  .settings(libraryDependencies ++= Seq(Json4sJackson, Json4sExt))
  .settings(libraryDependencies ++= Seq(Flume))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .dependsOn(core)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val consoleReporter = (project in file("cat4s-reporter-console"))
  .settings(name := "cat4s-reporter-console")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(LogbackClassic))
  .settings(libraryDependencies ++= Seq(Json4sJackson, Json4sExt))
  .settings(libraryDependencies ++= Seq(Flume))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .dependsOn(core)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val flumeReporter = (project in file("cat4s-reporter-flume"))
  .settings(name := "cat4s-reporter-flume")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(LogbackClassic))
  .settings(libraryDependencies ++= Seq(Json4sJackson, Json4sExt))
  .settings(libraryDependencies ++= Seq(Flume))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .dependsOn(core)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val fluentdReporter = (project in file("cat4s-reporter-fluentd"))
  .settings(name := "cat4s-reporter-fluentd")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(LogbackClassic))
  .settings(libraryDependencies ++= Seq(Json4sJackson, Json4sExt))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .dependsOn(core)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val logstashReporter = (project in file("cat4s-reporter-logstash"))
  .settings(name := "cat4s-reporter-logstash")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(LogbackClassic))
  .settings(libraryDependencies ++= Seq(Json4sJackson, Json4sExt))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .dependsOn(core)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val root = (project in file("."))
  .aggregate(aggregateProjects: _*)
  .settings(name := "cat4s")
  .settings(commonSettings: _*)
  .settings(documentationSettings: _*)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)
