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
import cat.plugin.logstash.LogstashReporter.MetricDoc
import cat4s.metric._
import cat4s.trace.TraceInfo

/**
 * @author siuming
 */
object LogstashReporter {
  val Name = "logstash-reporter"
  def props(): Props =
    Props(new LogstashReporter)

  object TraceDoc {
    def apply(): TraceDoc = new TraceDoc()
  }
  case class TraceDoc()

  object MetricDoc {
    def apply(
      serviceName: String,
      serviceHost: String,
      servicePort: String,
      sample: Sample,
      snapshot: SampleSnapshot): MetricDoc = {
      val metrics = snapshot.instruments
        .map(it => it._1.name -> it._2)
        .mapValues[Any] {
          case s: GaugeSnapshot         => s.value
          case s: CounterSnapshot       => s.value
          case s: MinMaxCounterSnapshot => Map("min" -> s.min, "max" -> s.max, "value" -> s.value)
          case s: MeterSnapshot         => s.rates ++ Map("count" -> s.count)
          case s: HistogramSnapshot     => s.percentiles ++ Map("min" -> s.min, "max" -> s.max, "mean" -> s.mean, "std_dev" -> s.stdDev)
          case s: TimerSnapshot         => s.rates ++ s.percentiles ++ Map("count" -> s.count, "min" -> s.min, "max" -> s.max, "mean" -> s.mean, "std_dev" -> s.stdDev)
          case s                        => s
        }

      val source = Map(
        "catalog" -> sample.catalog,
        "name" -> sample.name,
        "tags" -> sample.tags,
        "type" -> "metrics",
        "service_name" -> serviceName,
        "service_host" -> serviceHost,
        "service_port" -> servicePort
      )
      MetricDoc(metrics ++ source)
    }
  }
  case class MetricDoc(values: Map[String, Any])
}
class LogstashReporter extends Actor {
  private val settings = new LogstashSettings(context.system.settings.config)
  private val publisher = new LogstashPublisher(settings.destinations, settings.selector, settings.maxBufferSize, settings.maxRetries, settings.maxFails, settings.failTimeout)
  override def receive = {
    case info: MetricInfo =>
      info
        .samples
        .map(it => MetricDoc(settings.serviceName, settings.serviceHost, settings.servicePort, it._1, it._2).values)
        .foreach(publisher.publish)
    case info: TraceInfo =>
  }

  override def postStop(): Unit = {
    super.postStop()
    publisher.close()
  }
}
