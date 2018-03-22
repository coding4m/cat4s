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

package cat.plugin.logstash

import java.util.concurrent.atomic.AtomicLong

import scala.util.Random

/**
 * @author siuming
 */
trait LogstashSelector {
  def select(destinations: Seq[LogstashDestination]): LogstashDestination
}
object RandomSelector extends RandomSelector {
  val Name = "random"
}
trait RandomSelector extends LogstashSelector {
  private val random = new Random()
  override def select(destinations: Seq[LogstashDestination]) = {
    destinations(random.nextInt(destinations.size))
  }
}
object RoundRobinSelector extends RoundRobinSelector {
  val Name = "roundrobin"
}
trait RoundRobinSelector extends LogstashSelector {
  val counter = new AtomicLong()
  override def select(destinations: Seq[LogstashDestination]) = {
    destinations((counter.getAndIncrement() % destinations.size).toInt)
  }
}
