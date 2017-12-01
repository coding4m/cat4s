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

import com.codahale.metrics.Reservoir

/**
 * @author siuming
 */
class Timer(rates: Array[Long], percentiles: Array[Long], reservoir: Reservoir) extends Instrument {
  private val meter = new Meter(rates)
  private val histogram = new Histogram(percentiles, reservoir)

  override type Record = Long
  override type Snapshot = TimerSnapshot
  override def record(value: Long) = {
    if (value >= 0) {
      meter.record(value)
      histogram.record(value)
    }
  }
  override def collect(ctx: InstrumentContext) = {
    val ms = meter.collect(ctx)
    val hs = histogram.collect(ctx)
    TimerSnapshot(
      ms.count,
      ms.rates,
      hs.min,
      hs.max,
      hs.mean,
      hs.stdDev,
      hs.percentiles
    )
  }
}
