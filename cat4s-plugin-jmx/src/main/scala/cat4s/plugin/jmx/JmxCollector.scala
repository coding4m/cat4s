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

package cat4s.plugin.jmx

import java.lang.management.ManagementFactory

import akka.actor.{ Actor, Cancellable, Props }

import scala.concurrent.duration.FiniteDuration

/**
 * @author siuming
 */
private[jmx] object JmxCollector {
  val Name = "jmx-collector"
  def props(collectInterval: FiniteDuration, metrics: JmxMetrics): Props =
    Props(new JmxCollector(collectInterval, metrics))

  case object Collect
}
private[jmx] class JmxCollector(collectInterval: FiniteDuration, metrics: JmxMetrics) extends Actor {
  import scala.collection.JavaConverters._
  import JmxCollector.Collect

  val classCollector = ManagementFactory.getClassLoadingMXBean
  val memoryCollector = ManagementFactory.getMemoryMXBean
  val threadCollector = ManagementFactory.getThreadMXBean
  val garbageCollector = ManagementFactory.getGarbageCollectorMXBeans.asScala

  var collectScheduler: Option[Cancellable] = None

  override def receive = {
    case Collect => collect()
  }

  override def preStart(): Unit = {
    import context.dispatcher
    collect()
    collectScheduler = Some(context.system.scheduler.schedule(collectInterval, collectInterval, self, Collect))
  }

  override def postStop(): Unit = {
    collectScheduler.foreach(_.cancel())
  }

  private def collect(): Unit = {
    metrics.classesLoaded.record(classCollector.getLoadedClassCount.toLong)
    metrics.classesUnloaded.record(classCollector.getUnloadedClassCount)
    metrics.classesTotalLoaded.record(classCollector.getTotalLoadedClassCount)

    metrics.heapMemory.record(memoryCollector.getHeapMemoryUsage.getUsed)
    metrics.nonHeapMemory.record(memoryCollector.getNonHeapMemoryUsage.getUsed)

    metrics.threadCount.record(threadCollector.getThreadCount.toLong)
    metrics.daemonThreadCount.record(threadCollector.getDaemonThreadCount.toLong)

    garbageCollector.filter(_.isValid).foreach { gc =>
      metrics.gcCount(gc.getName).record(gc.getCollectionCount)
      metrics.gcTime(gc.getName).record(gc.getCollectionTime)
    }
  }
}
