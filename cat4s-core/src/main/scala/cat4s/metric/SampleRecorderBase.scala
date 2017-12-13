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

import codahale.metrics.Reservoir

import scala.collection.concurrent.TrieMap

/**
 * @author siuming
 */
abstract class SampleRecorderBase extends SampleRecorder {

  private val _instruments = TrieMap.empty[InstrumentKey, Instrument]
  private def register[T <: Instrument](key: InstrumentKey, instrument: => T): T =
    atomicGetOrElseUpdate(key, instrument, _.cleanup()).asInstanceOf[T]
  private def unregister(key: InstrumentKey): Unit =
    _instruments.remove(key).foreach(_.cleanup())

  protected def counter(name: String, resetAfterCollect: Boolean = false): Counter =
    register(CounterKey(name), new Counter(resetAfterCollect))
  protected def removeCounter(name: String): Unit =
    unregister(CounterKey(name))

  protected def minMaxCounter(name: String, resetAfterCollect: Boolean = true): MinMaxCounter =
    register(MinMaxCounterKey(name), new MinMaxCounter(resetAfterCollect))
  protected def removeMinMaxCounter(name: String): Unit =
    unregister(MinMaxCounterKey(name))

  protected def gauge(name: String, identity: Any, resetAfterCollect: Boolean = false): Gauge =
    register(GaugeKey(name), new Gauge(identity, resetAfterCollect))
  protected def removeGauge(name: String): Unit =
    unregister(GaugeKey(name))

  def meter(name: String, rates: Array[Long] = Meter.DefaultRates): Meter =
    register(MeterKey(name), new Meter(rates))
  def removeMeter(name: String): Unit =
    unregister(MeterKey(name))

  def timer(
    name: String,
    rates: Array[Long] = Timer.DefaultRates,
    percentiles: Array[Long] = Timer.DefaultPercentiles,
    reservoir: Reservoir = Timer.DefaultReservoir): Timer =
    register(TimerKey(name), new Timer(rates, percentiles, reservoir))
  def removeTimer(name: String): Unit =
    unregister(TimerKey(name))

  def histogram(
    name: String,
    percentiles: Array[Long] = Histogram.DefaultPercentiles,
    reservoir: Reservoir = Histogram.DefaultReservoir): Histogram =
    register(HistogramKey(name), new Histogram(percentiles, reservoir))
  def removeHistogram(name: String): Unit =
    unregister(HistogramKey(name))

  override def collect(ctx: InstrumentContext) = {
    val snapshots = Map.newBuilder[InstrumentKey, InstrumentSnapshot]
    _instruments.foreach {
      case (key, instrument) => snapshots += key -> instrument.collect(ctx)
    }
    SampleSnapshot(snapshots.result())
  }
  override def cleanup(): Unit = _instruments.values.foreach(_.cleanup())

  private def atomicGetOrElseUpdate(key: InstrumentKey, op: => Instrument, cleanup: Instrument => Unit): Instrument = {
    _instruments.get(key) match {
      case Some(v) => v
      case None =>
        val d = op
        _instruments.putIfAbsent(key, d).map { oldValue =>
          // If there was an old value then `d` was never added
          // and thus need to be cleanup.
          cleanup(d)
          oldValue

        } getOrElse d
    }
  }
}
