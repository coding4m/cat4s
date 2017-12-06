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

import akka.actor.ActorRef
import com.codahale.metrics.Reservoir

/**
 * @author siuming
 */
trait MetricSet {

  def counter(name: String, unit: InstrumentUnit): Counter = counter(name, unit, resetAfterCollect = false)
  def counter(name: String, unit: InstrumentUnit, resetAfterCollect: Boolean): Counter = counter(name, unit, resetAfterCollect, Seq.empty)
  def counter(name: String, unit: InstrumentUnit, resetAfterCollect: Boolean, tags: Seq[String]): Counter = registerCounter(name, unit, resetAfterCollect, tags)
  def registerCounter(name: String, unit: InstrumentUnit, resetAfterCollect: Boolean, tags: Seq[String]): Counter
  def unregisterCounter(name: String, tags: Seq[String]): Boolean

  def minMaxCounter(name: String, unit: InstrumentUnit): MinMaxCounter = minMaxCounter(name, unit, resetAfterCollect = false)
  def minMaxCounter(name: String, unit: InstrumentUnit, resetAfterCollect: Boolean): MinMaxCounter = minMaxCounter(name, unit, resetAfterCollect, Seq.empty)
  def minMaxCounter(name: String, unit: InstrumentUnit, resetAfterCollect: Boolean, tags: Seq[String]): MinMaxCounter = registerMinMaxCounter(name, unit, resetAfterCollect, tags)
  def registerMinMaxCounter(name: String, unit: InstrumentUnit, resetAfterCollect: Boolean, tags: Seq[String]): MinMaxCounter
  def unregisterMinMaxCounter(name: String, tags: Seq[String]): Boolean

  def gauge(name: String, unit: InstrumentUnit, identity: Any): Gauge = gauge(name, unit, identity, resetAfterCollect = false)
  def gauge(name: String, unit: InstrumentUnit, identity: Any, resetAfterCollect: Boolean): Gauge = gauge(name, unit, identity, resetAfterCollect, Seq.empty)
  def gauge(name: String, unit: InstrumentUnit, identity: Any, resetAfterCollect: Boolean, tags: Seq[String]): Gauge = registerGauge(name, unit, identity, resetAfterCollect, tags)
  def registerGauge(name: String, unit: InstrumentUnit, identity: Any, resetAfterCollect: Boolean, tags: Seq[String]): Gauge
  def unregisterGauge(name: String, tags: Seq[String]): Boolean

  def meter(name: String, unit: InstrumentUnit): Meter = meter(name, unit, Meter.DefaultRates)
  def meter(name: String, unit: InstrumentUnit, rates: Array[Long]): Meter = meter(name, unit, rates, Seq.empty)
  def meter(name: String, unit: InstrumentUnit, rates: Array[Long], tags: Seq[String]): Meter = registerMeter(name, unit, rates, tags)
  def registerMeter(name: String, unit: InstrumentUnit, rates: Array[Long], tags: Seq[String]): Meter
  def unregisterMeter(name: String, tags: Seq[String]): Boolean

  def timer(name: String, unit: InstrumentUnit): Timer = timer(name, unit, Timer.DefaultRates)
  def timer(name: String, unit: InstrumentUnit, rates: Array[Long]): Timer = timer(name, unit, rates, Timer.DefaultPercentiles)
  def timer(name: String, unit: InstrumentUnit, rates: Array[Long], percentiles: Array[Long]): Timer = timer(name, unit, rates, percentiles, Timer.DefaultReservoir)
  def timer(name: String, unit: InstrumentUnit, rates: Array[Long], percentiles: Array[Long], reservoir: Reservoir): Timer = timer(name, unit, rates, percentiles, reservoir, Seq.empty)
  def timer(name: String, unit: InstrumentUnit, rates: Array[Long], percentiles: Array[Long], reservoir: Reservoir, tags: Seq[String]): Timer = registerTimer(name, unit, rates, percentiles, reservoir, tags)
  def registerTimer(name: String, unit: InstrumentUnit, rates: Array[Long], percentiles: Array[Long], reservoir: Reservoir, tags: Seq[String]): Timer
  def unregisterTimer(name: String, tags: Seq[String]): Boolean

  def histogram(name: String, unit: InstrumentUnit): Histogram = histogram(name, unit, Histogram.DefaultPercentiles)
  def histogram(name: String, unit: InstrumentUnit, percentiles: Array[Long]): Histogram = histogram(name, unit, percentiles, Histogram.DefaultReservoir)
  def histogram(name: String, unit: InstrumentUnit, percentiles: Array[Long], reservoir: Reservoir): Histogram = histogram(name, unit, percentiles, reservoir, Seq.empty)
  def histogram(name: String, unit: InstrumentUnit, percentiles: Array[Long], reservoir: Reservoir, tags: Seq[String]): Histogram = registerHistogram(name, unit, percentiles, reservoir, tags)
  def registerHistogram(name: String, unit: InstrumentUnit, percentiles: Array[Long], reservoir: Reservoir, tags: Seq[String]): Histogram
  def unregisterHistogram(name: String, tags: Seq[String]): Boolean

  def sample[T <: SampleRecorder](rf: SampleRecorderFactory[T], name: String): T = sample(rf, name, Seq.empty)
  def sample[T <: SampleRecorder](rf: SampleRecorderFactory[T], name: String, tags: Seq[String]): T
  def removeSample[T <: SampleRecorder](rf: SampleRecorderFactory[T], name: String): Boolean = removeSample(name, rf.catelog)
  def removeSample(name: String, catelog: String): Boolean = removeSample(name, catelog, Seq.empty)
  def removeSample(name: String, catelog: String, tags: Seq[String]): Boolean = removeSample(Sample(name, catelog, tags))
  def removeSample(sample: Sample): Boolean

  def subscribe(subscriber: ActorRef, filter: MetricFilter, permanently: Boolean): Unit
  def unsubscribe(subscriber: ActorRef): Unit

  private[cat4s] def start()
  private[cat4s] def stop()
}
