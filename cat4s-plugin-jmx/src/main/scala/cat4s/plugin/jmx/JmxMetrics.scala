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

import cat4s.metric.{ SampleRecorderBase, SampleRecorderFactory }

/**
 * @author siuming
 */
private[jmx] object JmxMetrics extends SampleRecorderFactory[JmxMetrics] {
  val MinorGC = "minor"
  val MajorGC = "major"
  val GCNames = Map(
    "Copy" -> MinorGC, // Serial -XX:+UseSerialGC
    "ParNew" -> MinorGC, // CMS -XX:+UseParNewGC
    "PS Scavenge" -> MinorGC, // Throughput -XX:+UseParallelGC
    "G1 Young Generation" -> MinorGC, // G1 -XX:+UseG1GC

    "MarkSweepCompact" -> MajorGC, // Serial -XX:+UseSerialGC
    "ConcurrentMarkSweep" -> MajorGC, // CMS -XX:+UseConcMarkSweepGC
    "PS MarkSweep" -> MajorGC, // Throughput -XX:+UseParallelGC and (-XX:+UseParallelOldGC or -XX:+UseParallelOldGCCompacting)
    "G1 Old Generation" -> MajorGC // G1 -XX:+UseG1GC
  )

  override def catalog = "jmx"
  override def createRecorder() = new JmxMetrics
}
private[jmx] class JmxMetrics extends SampleRecorderBase {
  import JmxMetrics.GCNames
  val classesLoaded = gauge("classes_loaded", 0L)
  val classesUnloaded = gauge("classes_unloaded", 0L)
  val classesTotalLoaded = gauge("classes_total-loaded", 0L)

  val heapMemory = gauge("heap_memory", 0L)
  val nonHeapMemory = gauge("non_heap_memory", 0L)

  val threadCount = gauge("thread_count", 0L)
  val daemonThreadCount = gauge("daemon_thread_count", 0L)

  def gcCount(name: String) = gauge(s"${GCNames.getOrElse(name, name)}_gc_count", 0L)
  def gcTime(name: String) = gauge(s"${GCNames.getOrElse(name, name)}_gc_time", 0L)
}
