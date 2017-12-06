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

import java.util.concurrent.atomic.LongAdder

/**
 * @author siuming
 */
class Counter(resetAfterCollect: Boolean) extends Instrument {
  private val adder = new LongAdder
  override type Record = Long
  override type Snapshot = CounterSnapshot
  override def record(value: Long): Unit = adder.add(value)
  override def collect(ctx: InstrumentContext) = {
    if (resetAfterCollect) CounterSnapshot(adder.sumThenReset()) else CounterSnapshot(adder.sum())
  }
}
