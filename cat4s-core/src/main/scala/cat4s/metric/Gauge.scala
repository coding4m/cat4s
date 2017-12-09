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

import java.util.concurrent.atomic.AtomicReference

/**
 * @author siuming
 */
class Gauge(identity: Any, resetAfterCollect: Boolean) extends Instrument {
  private val _value = new AtomicReference[Any]

  override type Record = Any
  override type Snapshot = GaugeSnapshot
  override def record(value: Any): Unit = _value.set(value)
  override def collect(ctx: InstrumentContext) = {
    if (resetAfterCollect) GaugeSnapshot(_value.getAndSet(identity)) else GaugeSnapshot(_value.get())
  }
}
