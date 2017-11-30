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

import java.util.concurrent.atomic.{ LongAccumulator, LongAdder }
import java.util.function.LongBinaryOperator

/**
 * @author siuming
 */
class MinMaxCounter extends Instrument {
  private val min = new LongAccumulator(new LongBinaryOperator {
    override def applyAsLong(left: Long, right: Long) = Math.min(left, right)
  }, 0L)
  private val max = new LongAccumulator(new LongBinaryOperator {
    override def applyAsLong(left: Long, right: Long) = Math.max(left, right)
  }, 0L)
  private val count = new LongAdder
  override type Record = Long
  override type Snapshot = MinMaxCounterSnapshot
  override def record(value: Long) = {
    count.add(value)
    if (value < 0) {
      min.accumulate(count.sum())
    } else if (value > 0) {
      max.accumulate(count.sum())
    }
  }
  override def refresh() = {
    min.reset()
    max.reset()
    count.reset()
  }
  override def collect(ctx: InstrumentContext) = MinMaxCounterSnapshot(min.get(), max.get(), count.sum())
  override def cleanup() = {}
}
