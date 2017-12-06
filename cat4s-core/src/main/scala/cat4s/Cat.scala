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

package cat4s

import akka.actor.ActorSystem
import cat4s.metric.{ MetricRegistry, MetricSet }
import cat4s.trace.{ TraceRegistry, TraceSet }
import com.typesafe.config.{ Config, ConfigFactory }

/**
 * @author siuming
 */
object Cat {

  private val Name = "cat"
  @volatile private var instance = new Instance()

  def tracer: TraceSet = {
    val _tracer = instance.tracer
    if (null == _tracer) throw new CatException("cat must been started.") else _tracer
  }

  def metrics: MetricSet = {
    val _metrics = instance.metrics
    if (null == _metrics) throw new CatException("cat must been started.") else _metrics
  }

  def start(): Unit = {
    instance.start()
  }

  def start(config: Config): Unit = {
    instance.start(config)
  }

  def stop(): Unit = {
    instance.stop()
    instance = new Instance()
  }

  private class Instance {
    var actorSystem: ActorSystem = _
    var tracer: TraceSet = _
    var metrics: MetricSet = _
    var started = false

    def start(): Unit = {
      start(ConfigFactory.load())
    }

    def start(config: Config): Unit = this.synchronized {
      actorSystem = ActorSystem(Name, config.withOnlyPath(Name))
      tracer = actorSystem.registerExtension(TraceRegistry)
      metrics = actorSystem.registerExtension(MetricRegistry)
      actorSystem.registerExtension(PluginLoader)
      actorSystem.registerExtension(ReporterLoader)
      tracer.start()
      metrics.start()
      started = true
    }

    def stop(): Unit = this.synchronized {
      if (started) {
        started = false
        actorSystem.terminate()
      }
    }
  }
}
