/*
 * Copyright 2015 - 2016 Forever High Tech <http://www.foreverht.com> - all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.nio.file.StandardOpenOption._
import java.nio.file._

import com.typesafe.sbt.SbtGhPages.ghpages
import com.typesafe.sbt.SbtGit.git
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.{connectInput => multiJvmConnectInput, _}
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.SbtSite.site
import com.typesafe.sbt.{SbtMultiJvm, SbtScalariform}
import de.heikoseeberger.sbtheader.HeaderKey._
import de.heikoseeberger.sbtheader.license.Apache2_0
import de.heikoseeberger.sbtheader.{AutomateHeaderPlugin, HeaderPlugin}
import sbt.Keys._
import sbt._
import sbtprotobuf.ProtobufPlugin
import sbtunidoc.Plugin.{ScalaUnidoc, unidocSettings}

import scalariform.formatter.preferences.{AlignSingleLineCaseStatements, DanglingCloseParenthesis, DoubleIndentClassDeclaration, Preserve}

object ProjectSettings {

  lazy val rootSettings: Seq[Setting[_]] =
    compilerSettings ++
      headerSettings ++
      testSettings ++
      publishSettings

  lazy val commonSettings: Seq[Setting[_]] =
    rootSettings ++ formatterSettings ++ Seq(
      resolvers += "DDDLib Releases Group" at "http://nexus.workplus.io:8081/repository/maven-releases/",
      resolvers += "DDDLib Snapshots Group" at "http://nexus.workplus.io:8081/repository/maven-snapshots/"
    )

  lazy val integrationTestSettings: Seq[Setting[_]] =
    integrationTestHeaderSettings ++
      integrationTestSingleNodeSettings ++
      integrationTestMultiNodeSettings ++
      integrationTestFormatterSettings

  // ----------------------------------------------------------------------
  //  Common settings
  // ----------------------------------------------------------------------

  lazy val compilerSettings: Seq[Setting[_]] = Seq(
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation", "-Xlint")
  )

  lazy val testSettings: Seq[Setting[_]] = Seq(
    parallelExecution in Test := false,
    fork in Test := true
  )

  lazy val headerSettings: Seq[Setting[_]] = {
    val header = Apache2_0("2017 - 2018", "Forever High Tech <http://www.foreverht.com> - all rights reserved.")

    Seq(headers := Map(
      "scala" -> header,
      "java" -> header)
    )
  }

  // ----------------------------------------------------------------------
  //  Integration test settings
  // ----------------------------------------------------------------------

  lazy val integrationTestSingleNodeSettings = Defaults.itSettings ++ Seq(
    createHeaders in IntegrationTest <<= (createHeaders in IntegrationTest) triggeredBy (createHeaders in Test),
    compile in IntegrationTest <<= (compile in IntegrationTest) triggeredBy (compile in Test),
    test in IntegrationTest <<= (test in IntegrationTest) triggeredBy (test in Test),
    parallelExecution in IntegrationTest := false,
    fork in IntegrationTest := true
  )

  lazy val integrationTestMultiNodeSettings = SbtMultiJvm.multiJvmSettings ++ Seq(
    createHeaders in MultiJvm <<= (createHeaders in MultiJvm) triggeredBy (createHeaders in Test),
    compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
    executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
      case (testResults, multiJvmResults) =>
        val overall =
          if (testResults.overall.id < multiJvmResults.overall.id) multiJvmResults.overall
          else testResults.overall
        Tests.Output(overall,
          testResults.events ++ multiJvmResults.events,
          testResults.summaries ++ multiJvmResults.summaries)
    }
  )

  lazy val integrationTestHeaderSettings: Seq[Setting[_]] =
    HeaderPlugin.settingsFor(IntegrationTest, MultiJvm) ++
      AutomateHeaderPlugin.automateFor(IntegrationTest, MultiJvm)

  lazy val integrationTestPublishSettings = Seq(
    publishArtifact in Test := true
  ) ++ Classpaths.defaultPackageKeys.flatMap(tk => addArtifact(artifact in(IntegrationTest, tk), tk in IntegrationTest))

  // ----------------------------------------------------------------------
  //  Code formatter settings
  // ----------------------------------------------------------------------

  lazy val formatterSettings: Seq[Setting[_]] = {

    SbtScalariform.scalariformSettings ++ Seq(
      ScalariformKeys.preferences := ScalariformKeys.preferences.value
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(DanglingCloseParenthesis, Preserve)
        .setPreference(DoubleIndentClassDeclaration, false)
    )
  }

  lazy val integrationTestFormatterSettings: Seq[Setting[_]] = {
    SbtScalariform.scalariformSettingsWithIt ++ Seq(
      compileInputs in(MultiJvm, compile) <<= (compileInputs in(MultiJvm, compile)) dependsOn (ScalariformKeys.format in MultiJvm)
    ) ++ inConfig(MultiJvm)(SbtScalariform.configScalariformSettings)
  }

  // ----------------------------------------------------------------------
  //  Publish settings
  // ----------------------------------------------------------------------

  //  lazy val publishSettings: Seq[Setting[_]] = {
  //    val jFrogCredentials = Credentials(
  //      "Artifactory Realm",
  //      "oss.jfrog.org",
  //      sys.env.getOrElse("OSS_JFROG_USER", ""),
  //      sys.env.getOrElse("OSS_JFROG_PASS", "")
  //    )
  //
  //    val jFrogBase =
  //      "https://oss.jfrog.org/artifactory/"
  //
  //    val jFrogSnapshotRepo = Some("OJO Snapshots" at jFrogBase + "oss-snapshot-local")
  //    val jFrogReleaseRepo = Some("OJO Releases" at jFrogBase + "oss-release-local")
  //
  //    Seq(
  //      credentials += jFrogCredentials,
  //      publishTo := (if (isSnapshot.value) jFrogSnapshotRepo else jFrogReleaseRepo),
  //      publishMavenStyle := true
  //    )
  //  }

  lazy val publishSettings: Seq[Setting[_]] = {
    val dddlibCredentials = Credentials(
      "Sonatype Nexus Repository Manager",
      "nexus.workplus.io",
      sys.env.getOrElse("DDDLIB_USER", ""),
      sys.env.getOrElse("DDDLIB_PASS", "")
    )

    val dddlibBase = "http://nexus.workplus.io:8081/repository"
    val dddlibSnapshotRepo = Some("DDDLib Snapshots" at dddlibBase+ "/maven-snapshots/")
    val dddlibReleaseRepo = Some("DDDLib Releases" at dddlibBase + "/maven-releases/")

    Seq(
      credentials += dddlibCredentials,
      publishTo := (if (isSnapshot.value) dddlibSnapshotRepo else dddlibReleaseRepo),
      publishMavenStyle := true
    )
  }

  // ----------------------------------------------------------------------
  //  Documentation settings
  // ----------------------------------------------------------------------
//
//  lazy val documentationSettings: Seq[Setting[_]] =
//    unidocSettings ++
//      site.settings ++
//      site.sphinxSupport() ++
//      site.includeScaladoc() ++
//      ghpages.settings ++
//      Seq(
//        git.remoteRepo := "git@github.com:coding4m/cat4s.git",
//        site.addMappingsToSiteDir(mappings in(ScalaUnidoc, packageDoc), "latest/api"),
//        unmanagedSourceDirectories in Test += baseDirectory.value / "src" / "sphinx" / "code",
//        scalacOptions in(Compile, doc) := List("-skip-packages", "akka")
//      )

  // ----------------------------------------------------------------------
  //  Example application settings
  // ----------------------------------------------------------------------

  lazy val exampleSettings: Seq[Setting[_]] = {
    lazy val generateClasspath = taskKey[Unit]("generate example classpath script")

    Seq(
      generateClasspath <<= generateClasspath.dependsOn(sbt.Keys.compile in Test),
      generateClasspath := {
        val cpaths = (fullClasspath in Compile).value.map(_.data).mkString(":")
        val output =
          s"""|#!/bin/sh
              |
              |# this file is automatically generated by the sbt 'generateClasspath' command
              |
              |export EXAMPLE_CLASSPATH="$cpaths"
              |""".stripMargin

        val file = Paths.get(baseDirectory.value.toString, "bin", ".example-classpath")

        Files.write(file, output.getBytes, CREATE, TRUNCATE_EXISTING)
        file.toFile.setExecutable(true)
      },
      fork in run := true,
      connectInput in run := true
    )
  }
}