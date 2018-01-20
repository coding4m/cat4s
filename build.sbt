import ProjectDependencies._
import ProjectSettings._
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

name := "cat4s"

version in ThisBuild := "1.0.0-SNAPSHOT"

organization in ThisBuild := "cat4s"

scalaVersion in ThisBuild := "2.12.4"
crossScalaVersions := Seq("2.12.4", "2.11.12")

javacOptions in ThisBuild ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

externalResolvers in ThisBuild := Resolver.withDefaultResolvers(Seq.empty, mavenCentral = true)

isSnapshot in ThisBuild := true

//fork in ThisBuild := true

lazy val aggregateProjects: Seq[ProjectReference] = Seq(
  aspectj,
  core,
  akkaPlugin,
  jmxPlugin,
  jdbcPlugin,
  logstashPlugin,
  consolePlugin
)

lazy val aspectj = (project in file("cat4s-aspectj"))
  .settings(name := "cat4s-aspectj")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(AspectJWeaver))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val core = (project in file("cat4s-core"))
  .settings(name := "cat4s-core")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(AkkaActor))
  .settings(libraryDependencies ++= Seq(AspectJRuntime))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
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


lazy val jmxPlugin = (project in file("cat4s-plugin-jmx"))
  .settings(name := "cat4s-plugin-jmx")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(LogbackClassic))
  .settings(libraryDependencies ++= Seq(Json4sJackson, Json4sExt))
  .settings(libraryDependencies ++= Seq(Flume))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .dependsOn(core)
  .dependsOn(consolePlugin % "test")
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

lazy val consolePlugin = (project in file("cat4s-plugin-console"))
  .settings(name := "cat4s-plugin-console")
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


lazy val logstashPlugin = (project in file("cat4s-plugin-logstash"))
  .settings(name := "cat4s-plugin-logstash")
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
//  .settings(documentationSettings: _*)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)
