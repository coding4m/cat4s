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

package cat4s.plugin.os

import java.lang.management.ManagementFactory

import akka.actor.{ Actor, Props }

/**
 * @author siuming
 */
private[os] object JvmCollector {
  val Name = "jvm-collector"
  def props(metrics: JvmMetrics): Props =
    Props(new JvmCollector(metrics))
}
private[os] class JvmCollector(metrics: JvmMetrics) extends Actor {
  import scala.collection.JavaConverters._

  val classCollector = ManagementFactory.getClassLoadingMXBean
  val memoryCollector = ManagementFactory.getMemoryMXBean
  val threadCollector = ManagementFactory.getThreadMXBean
  val garbageCollector = ManagementFactory.getGarbageCollectorMXBeans.asScala

  override def receive = {
    case _ =>
  }

  override def preStart(): Unit = collect()

  private def collect(): Unit = {
    metrics.classesLoaded.record(classCollector.getLoadedClassCount.toLong)
    metrics.classesUnloaded.record(classCollector.getUnloadedClassCount)
    metrics.classesTotalLoaded.record(classCollector.getTotalLoadedClassCount)

    metrics.heapMemory.record(memoryCollector.getHeapMemoryUsage.getUsed)
    metrics.nonHeapMemory.record(memoryCollector.getNonHeapMemoryUsage.getUsed)

    metrics.threadCount.record(threadCollector.getThreadCount.toLong)
    metrics.daemonThreadCount.record(threadCollector.getDaemonThreadCount.toLong)

    garbageCollector.filter(_.isValid).foreach { gc =>
      metrics.garbageCollectCount(gc.getName).record(gc.getCollectionCount)
      metrics.garbageCollectTime(gc.getName).record(gc.getCollectionTime)
    }
  }
}
