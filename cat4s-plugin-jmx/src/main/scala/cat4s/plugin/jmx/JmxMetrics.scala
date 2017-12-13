package cat4s.plugin.jmx

import cat4s.metric.{SampleRecorderBase, SampleRecorderFactory}

/**
  * @author siuming
  */
object JmxMetrics extends SampleRecorderFactory[JmxMetrics]{
  override def catalog = "jmx"
  override def createRecorder() = new JmxMetrics
}
class JmxMetrics extends SampleRecorderBase{
  val classesLoaded = gauge("classes-loaded", 0L)
  val classesUnloaded = gauge("classes-unloaded", 0L)
  val classesTotalLoaded = gauge("classes-total-loaded", 0L)

  val heapMemory = gauge("heap-memory", 0L)
  val nonHeapMemory = gauge("non-heap-memory", 0L)

  val threadCount = gauge("thread-count", 0L)
  val daemonThreadCount = gauge("daemon-thread-count", 0L)

  def gcCount(name: String) = gauge(s"$name-gc-count", 0L)
  def gcTime(name: String) = gauge(s"$name-gc-time", 0L)
}
