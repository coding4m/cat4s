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

/**
 * @author siuming
 */
object Histogram {
  val DefaultReservoir = new ExponentiallyDecayingReservoir()
  val DefaultPercentiles = Array(1L, 5L, 10L, 90L, 95L, 99L)
}
class Histogram(percentiles: Array[Long], reservoir: Reservoir) extends Instrument {
  require(percentiles.forall(_ > 0) && percentiles.forall(_ <= 100), "percentile must be in (0..100].")
  require(null != reservoir, "reservoir must not be null.")

  override type Record = Long
  override type Snapshot = HistogramSnapshot
  override def record(value: Long): Unit = reservoir.update(value)
  override def collect(ctx: InstrumentContext) = {
    val snapshot = reservoir.getSnapshot
    HistogramSnapshot(
      snapshot.getMin,
      snapshot.getMax,
      snapshot.getMean,
      snapshot.getStdDev,
      percentiles = percentiles.map(it => s"p$it" -> snapshot.getValue((it.toDouble / 100).formatted("%.2f").toDouble)).toMap
    )
  }
  override def cleanup(): Unit = {}
}
