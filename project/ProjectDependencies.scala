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

import sbt._

object ProjectDependencyVersions {
  val DisruptorVersion = "3.3.7"
  val LogbackVersion = "1.2.3"
  val Json4sVersion = "3.5.3"
  val FlumeVersion = "1.8.0"
}

object ProjectDependencies {

  import ProjectDependencyVersions._

  val Scalatest =            "org.scalatest"             %% "scalatest"                 % "3.0.0"
  val Scalaz =               "org.scalaz"                %% "scalaz-core"               % "7.2.7"

  val Javaslang =            "io.javaslang"               % "javaslang"                 % "2.0.5"
  val JunitInterface =       "com.novocode"               % "junit-interface"           % "0.11"

  val LogbackCore = "ch.qos.logback" % "logback-core" % LogbackVersion
  val LogbackAccess = "ch.qos.logback" % "logback-access" % LogbackVersion
  val LogbackClassic = "ch.qos.logback" % "logback-classic" % LogbackVersion

  val Json4sJackson =        "org.json4s"                %% "json4s-jackson"            % Json4sVersion
  val Json4sExt =            "org.json4s"                %% "json4s-ext"                % Json4sVersion

  val Disruptor = "com.lmax" % "disruptor" % DisruptorVersion
  val Flume =  "org.apache.flume" % "flume-ng-sdk" % FlumeVersion
}

