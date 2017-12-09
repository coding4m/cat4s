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

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.{ AtomicLong, LongAdder }

import codahale.metrics.{ Clock, EWMA }

/**
 * @author siuming
 */
object Meter {
  val DefaultRates = Array(1L, 5L, 15L)

  private[metric] val Interval = 5
  private[metric] val TicketInterval = TimeUnit.SECONDS.toNanos(Interval)
  private[metric] val SecondsPerMinute = 60.0d

  private[metric] def ewma(rate: Long): EWMA =
    new EWMA(1 - Math.exp(-Interval / SecondsPerMinute / rate), Interval, TimeUnit.SECONDS)
}
class Meter(rates: Array[Long]) extends Instrument {

  private val count = new LongAdder
  private val ewmas = rates.map(it => it -> Meter.ewma(it)).toMap

  private val clock = Clock.defaultClock()
  private val startTick = clock.getTick
  private val lastTick = new AtomicLong(startTick)

  override type Record = Long
  override type Snapshot = MeterSnapshot
  override def record(value: Long): Unit = {
    tickIfNecessary()
    count.add(value)
    ewmas.values.foreach(_.update(value))
  }
  override def collect(ctx: InstrumentContext) = {
    tickIfNecessary()
    MeterSnapshot(
      count.sum(),
      rates = ewmas.map(it => s"r${it._1}" -> it._2.getRate(TimeUnit.SECONDS))
    )
  }
  private def tickIfNecessary(): Unit = {
    val oldTick = lastTick.get
    val newTick = clock.getTick
    val age = newTick - oldTick
    if (age > Meter.TicketInterval) {
      val newIntervalStartTick = newTick - age % Meter.TicketInterval
      if (lastTick.compareAndSet(oldTick, newIntervalStartTick)) {
        val requiredTicks = age / Meter.TicketInterval
        for (_ <- 0 until requiredTicks.toInt) {
          ewmas.values.foreach(_.tick())
        }
      }
    }
  }
}
