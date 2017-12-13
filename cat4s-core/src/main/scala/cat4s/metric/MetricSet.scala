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

import akka.actor.ActorRef
import codahale.metrics.Reservoir

/**
 * @author siuming
 */
trait MetricSet {

  def counter(name: String): Counter = counter(name, resetAfterCollect = false)
  def counter(name: String, resetAfterCollect: Boolean): Counter = counter(name, resetAfterCollect, Seq.empty)
  def counter(name: String, resetAfterCollect: Boolean, tags: Seq[String]): Counter = registerCounter(name, resetAfterCollect, tags)
  def registerCounter(name: String, resetAfterCollect: Boolean, tags: Seq[String]): Counter
  def unregisterCounter(name: String, tags: Seq[String]): Boolean

  def minMaxCounter(name: String): MinMaxCounter = minMaxCounter(name, resetAfterCollect = false)
  def minMaxCounter(name: String, resetAfterCollect: Boolean): MinMaxCounter = minMaxCounter(name, resetAfterCollect, Seq.empty)
  def minMaxCounter(name: String, resetAfterCollect: Boolean, tags: Seq[String]): MinMaxCounter = registerMinMaxCounter(name, resetAfterCollect, tags)
  def registerMinMaxCounter(name: String, resetAfterCollect: Boolean, tags: Seq[String]): MinMaxCounter
  def unregisterMinMaxCounter(name: String, tags: Seq[String]): Boolean

  def gauge(name: String, identity: Any): Gauge = gauge(name, identity, resetAfterCollect = false)
  def gauge(name: String, identity: Any, resetAfterCollect: Boolean): Gauge = gauge(name, identity, resetAfterCollect, Seq.empty)
  def gauge(name: String, identity: Any, resetAfterCollect: Boolean, tags: Seq[String]): Gauge = registerGauge(name, identity, resetAfterCollect, tags)
  def registerGauge(name: String, identity: Any, resetAfterCollect: Boolean, tags: Seq[String]): Gauge
  def unregisterGauge(name: String, tags: Seq[String]): Boolean

  def meter(name: String): Meter = meter(name, Meter.DefaultRates)
  def meter(name: String, rates: Array[Long]): Meter = meter(name, rates, Seq.empty)
  def meter(name: String, rates: Array[Long], tags: Seq[String]): Meter = registerMeter(name, rates, tags)
  def registerMeter(name: String, rates: Array[Long], tags: Seq[String]): Meter
  def unregisterMeter(name: String, tags: Seq[String]): Boolean

  def timer(name: String): Timer = timer(name, Timer.DefaultRates)
  def timer(name: String, rates: Array[Long]): Timer = timer(name, rates, Timer.DefaultPercentiles)
  def timer(name: String, rates: Array[Long], percentiles: Array[Long]): Timer = timer(name, rates, percentiles, Timer.DefaultReservoir)
  def timer(name: String, rates: Array[Long], percentiles: Array[Long], reservoir: Reservoir): Timer = timer(name, rates, percentiles, reservoir, Seq.empty)
  def timer(name: String, rates: Array[Long], percentiles: Array[Long], reservoir: Reservoir, tags: Seq[String]): Timer = registerTimer(name, rates, percentiles, reservoir, tags)
  def registerTimer(name: String, rates: Array[Long], percentiles: Array[Long], reservoir: Reservoir, tags: Seq[String]): Timer
  def unregisterTimer(name: String, tags: Seq[String]): Boolean

  def histogram(name: String): Histogram = histogram(name, Histogram.DefaultPercentiles)
  def histogram(name: String, percentiles: Array[Long]): Histogram = histogram(name, percentiles, Histogram.DefaultReservoir)
  def histogram(name: String, percentiles: Array[Long], reservoir: Reservoir): Histogram = histogram(name, percentiles, reservoir, Seq.empty)
  def histogram(name: String, percentiles: Array[Long], reservoir: Reservoir, tags: Seq[String]): Histogram = registerHistogram(name, percentiles, reservoir, tags)
  def registerHistogram(name: String, percentiles: Array[Long], reservoir: Reservoir, tags: Seq[String]): Histogram
  def unregisterHistogram(name: String, tags: Seq[String]): Boolean

  def sample[T <: SampleRecorder](rf: SampleRecorderFactory[T], name: String): T = sample(rf, name, Seq.empty)
  def sample[T <: SampleRecorder](rf: SampleRecorderFactory[T], name: String, tags: Seq[String]): T
  def removeSample[T <: SampleRecorder](rf: SampleRecorderFactory[T], name: String): Boolean = removeSample(name, rf.catalog)
  def removeSample(name: String, catelog: String): Boolean = removeSample(name, catelog, Seq.empty)
  def removeSample(name: String, catelog: String, tags: Seq[String]): Boolean = removeSample(Sample(name, catelog, tags))
  def removeSample(sample: Sample): Boolean

  def subscribe(subscriber: ActorRef): Unit
  def unsubscribe(subscriber: ActorRef): Unit

  private[cat4s] def start()
  private[cat4s] def stop()
}
