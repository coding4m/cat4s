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

package cat4s.metric
import akka.actor.{ ActorRef, Cancellable, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }
import codahale.metrics.Reservoir

import scala.collection.concurrent.TrieMap

/**
 * @author siuming
 */
object MetricRegistry extends ExtensionId[MetricRegistry] with ExtensionIdProvider {
  override def lookup() = MetricRegistry
  override def createExtension(system: ExtendedActorSystem) = new MetricRegistry(system)
}
class MetricRegistry(system: ExtendedActorSystem) extends Extension with MetricSet {
  import SubscriptionProtocol._
  import SubscriptionController._

  private val samples = TrieMap.empty[Sample, SampleRecorder]
  private val settings = new MetricSettings(system.settings.config)
  private val controller = system.actorOf(SubscriptionController.props(), SubscriptionController.Name)

  @volatile private var collector: Option[Cancellable] = None

  override def registerCounter(name: String, resetAfterCollect: Boolean, tags: Seq[String]) = {
    atomicGetOrElseUpdate(Sample(name, InstrumentCatalog.Counter, tags), {
      new CounterRecorder(CounterKey(name), new Counter(resetAfterCollect))
    }, _.cleanup()).asInstanceOf[CounterRecorder].instrument
  }
  override def unregisterCounter(name: String, tags: Seq[String]) = removeSample(name, InstrumentCatalog.Counter, tags)

  override def registerMinMaxCounter(name: String, resetAfterCollect: Boolean, tags: Seq[String]) = {
    atomicGetOrElseUpdate(Sample(name, InstrumentCatalog.MinMaxCounter, tags), {
      new MinMaxCounterRecorder(MinMaxCounterKey(name), new MinMaxCounter(resetAfterCollect))
    }, _.cleanup()).asInstanceOf[MinMaxCounterRecorder].instrument
  }
  override def unregisterMinMaxCounter(name: String, tags: Seq[String]) = removeSample(name, InstrumentCatalog.MinMaxCounter, tags)

  override def registerGauge(name: String, identity: Any, resetAfterCollect: Boolean, tags: Seq[String]) = {
    atomicGetOrElseUpdate(Sample(name, InstrumentCatalog.Gauge, tags), {
      new GaugeRecorder(GaugeKey(name), new Gauge(identity, resetAfterCollect))
    }, _.cleanup()).asInstanceOf[GaugeRecorder].instrument
  }
  override def unregisterGauge(name: String, tags: Seq[String]) = removeSample(name, InstrumentCatalog.Gauge, tags)

  override def registerMeter(name: String, rates: Array[Long], tags: Seq[String]) = {
    atomicGetOrElseUpdate(Sample(name, InstrumentCatalog.Meter, tags), {
      new MeterRecorder(MeterKey(name), new Meter(rates))
    }, _.cleanup()).asInstanceOf[MeterRecorder].instrument
  }
  override def unregisterMeter(name: String, tags: Seq[String]) = removeSample(name, InstrumentCatalog.Meter, tags)

  override def registerTimer(name: String, rates: Array[Long], percentiles: Array[Long], reservoir: Reservoir, tags: Seq[String]) = {
    atomicGetOrElseUpdate(Sample(name, InstrumentCatalog.Timer, tags), {
      new TimerRecorder(TimerKey(name), new Timer(rates, percentiles, reservoir))
    }, _.cleanup()).asInstanceOf[TimerRecorder].instrument
  }
  override def unregisterTimer(name: String, tags: Seq[String]) = removeSample(name, InstrumentCatalog.Timer, tags)

  override def registerHistogram(name: String, percentiles: Array[Long], reservoir: Reservoir, tags: Seq[String]) = {
    atomicGetOrElseUpdate(Sample(name, InstrumentCatalog.Histogram, tags), {
      new HistogramRecorder(HistogramKey(name), new Histogram(percentiles, reservoir))
    }, _.cleanup()).asInstanceOf[HistogramRecorder].instrument
  }
  override def unregisterHistogram(name: String, tags: Seq[String]) = removeSample(name, InstrumentCatalog.Histogram, tags)

  override def sample[T <: SampleRecorder](rf: SampleRecorderFactory[T], name: String, tags: Seq[String]) = {
    atomicGetOrElseUpdate(Sample(name, rf.catalog, tags), rf.createRecorder(), _.cleanup()).asInstanceOf[T]
  }
  override def removeSample(sample: Sample) = {
    val recorder = samples.remove(sample)
    recorder.foreach(_.cleanup())
    recorder.isDefined
  }

  override def subscribe(subscriber: ActorRef): Unit = controller ! Subscribe(subscriber)
  override def unsubscribe(subscriber: ActorRef): Unit = controller ! Unsubscribe(subscriber)

  override private[cat4s] def start(): Unit = {
    import system.dispatcher
    collector.foreach(_.cancel())
    collector = Some(system.scheduler.schedule(settings.collectInterval, settings.collectInterval) {
      val ctx = InstrumentContext(settings.collectBufferSize)
      val builder = Map.newBuilder[Sample, SampleSnapshot]
      samples.foreach {
        case (identity, recorder) => builder += (identity -> recorder.collect(ctx))
      }
      val metrics = builder.result()
      if (metrics.nonEmpty) {
        controller ! MetricInfo(metrics)
      }
    })

    controller ! Process
  }
  override private[cat4s] def stop(): Unit = collector.foreach(_.cancel())

  private def atomicGetOrElseUpdate(key: Sample, op: => SampleRecorder, cleanup: SampleRecorder => Unit): SampleRecorder = {
    samples.get(key) match {
      case Some(v) => v
      case None =>
        val d = op
        samples.putIfAbsent(key, d).map { oldValue =>
          // If there was an old value then `d` was never added
          // and thus need to be cleanup.
          cleanup(d)
          oldValue

        } getOrElse d
    }
  }
}
