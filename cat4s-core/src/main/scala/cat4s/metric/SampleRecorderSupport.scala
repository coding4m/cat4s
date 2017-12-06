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

package cat4s.metric

import com.codahale.metrics.{ ExponentiallyDecayingReservoir, Reservoir }

import scala.collection.concurrent.TrieMap

/**
 * @author siuming
 */
abstract class SampleRecorderSupport(instrumentFactory: InstrumentFactory) extends SampleRecorder {

  private val _instruments = TrieMap.empty[InstrumentKey, Instrument]
  private def register[T <: Instrument](key: InstrumentKey, instrument: ⇒ T): T = ???
  private def unregister(key: InstrumentKey): Unit = _instruments.remove(key).foreach(_.cleanup())

  protected def counter(name: String, unit: InstrumentUnit, resetAfterCollect: Boolean = false): Counter =
    register(CounterKey(name, unit), new Counter(resetAfterCollect))
  protected def removeCounter(name: String, unit: InstrumentUnit): Unit =
    unregister(CounterKey(name, unit))

  protected def minMaxCounter(name: String, unit: InstrumentUnit, resetAfterCollect: Boolean = true): MinMaxCounter =
    register(MinMaxCounterKey(name, unit), new MinMaxCounter(resetAfterCollect))
  protected def removeMinMaxCounter(name: String, unit: InstrumentUnit): Unit =
    unregister(MinMaxCounterKey(name, unit))

  protected def gauge(name: String, unit: InstrumentUnit, identity: Any, resetAfterCollect: Boolean = false): Gauge =
    register(GaugeKey(name, unit), new Gauge(identity, resetAfterCollect))
  protected def removeGauge(name: String, unit: InstrumentUnit): Unit =
    unregister(GaugeKey(name, unit))

  def meter(name: String, unit: InstrumentUnit, rates: Array[Long] = Meter.DefaultRates): Meter =
    register(MeterKey(name, unit), new Meter(rates))
  def removeMeter(name: String, unit: InstrumentUnit): Unit =
    unregister(MeterKey(name, unit))

  def timer(
    name: String,
    unit: InstrumentUnit,
    rates: Array[Long] = Meter.DefaultRates,
    percentiles: Array[Long] = Histogram.DefaultPercentiles,
    reservoir: Reservoir = Histogram.DefaultReservoir): Timer =
    register(TimerKey(name, unit), new Timer(rates, percentiles, reservoir))
  def removeTimer(name: String, unit: InstrumentUnit): Unit =
    unregister(TimerKey(name, unit))

  def histogram(
    name: String,
    unit: InstrumentUnit,
    percentiles: Array[Long] = Histogram.DefaultPercentiles,
    reservoir: Reservoir = Histogram.DefaultReservoir): Histogram =
    register(HistogramKey(name, unit), new Histogram(percentiles, reservoir))
  def removeHistogram(name: String, unit: InstrumentUnit): Unit =
    unregister(HistogramKey(name, unit))

  override def collect(ctx: InstrumentContext) = {
    val snapshots = Map.newBuilder[InstrumentKey, InstrumentSnapshot]
    _instruments.foreach {
      case (key, instrument) => snapshots += key -> instrument.collect(ctx)
    }
    SampleSnapshot(snapshots.result())
  }
  override def cleanup() = _instruments.values.foreach(_.cleanup())
}
