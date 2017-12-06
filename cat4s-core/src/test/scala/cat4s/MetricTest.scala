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

package cat4s

import scala.io.Source
import scala.util.Random

/**
 * @author siuming
 */
object MetricTest extends App {
  val lines = Source.stdin.getLines

  Cat.start()
  val c0 = Cat.metrics.counter("c0", null)
  val c1 = Cat.metrics.counter("c1", null)
  val m0 = Cat.metrics.meter("m0", null)
  val m1 = Cat.metrics.meter("m1", null)
  val h0 = Cat.metrics.histogram("h0", null)
  val h1 = Cat.metrics.histogram("h0", null)
  val mc0 = Cat.metrics.minMaxCounter("mc0", null)
  val mc1 = Cat.metrics.minMaxCounter("mc1", null)
  prompt()

  private def prompt(): Unit = {
    if (lines.hasNext) lines.next() match {
      case "exit" =>
        Cat.stop()
        System.exit(0)
      case "record" =>
        c0.record(1)
        c1.record(1)
        m0.record(Random.nextInt(100))
        m1.record(Random.nextInt(100))
        h0.record(Random.nextInt(200))
        h1.record(Random.nextInt(200))
        mc0.record(10 - Random.nextInt(20))
        mc1.record(10 - Random.nextInt(30))
        prompt()
      case _ =>
        prompt()
    }
  }
}
