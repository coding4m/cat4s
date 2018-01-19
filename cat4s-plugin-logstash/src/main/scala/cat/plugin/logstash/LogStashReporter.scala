/*
 * Copyright 2017 - 2018 Forever High Tech <http://www.foreverht.com> - all rights reserved.
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

package cat.plugin.logstash

import akka.actor.{ Actor, Props }
import cat4s.metric.MetricInfo
import cat4s.trace.TraceInfo

/**
 * @author siuming
 */
object LogStashReporter {
  val Name = "logstash-reporter"
  def props(): Props =
    Props(new LogStashReporter)
}
class LogStashReporter extends Actor {
  private val settings = new LogStashSettings(context.system.settings.config)
  override def receive = {
    case info: TraceInfo  =>
    case info: MetricInfo =>
  }
}
