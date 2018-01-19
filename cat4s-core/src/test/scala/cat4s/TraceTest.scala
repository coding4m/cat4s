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

package cat4s

import scala.io.Source
import scala.util.Random

/**
 * @author siuming
 */
object TraceTest extends App {
  val lines = Source.stdin.getLines

  Cat.start()
  prompt()

  private def prompt(): Unit = {
    if (lines.hasNext) lines.next() match {
      case "exit" =>
        Cat.stop()
        System.exit(0)
      case "record" =>
        Cat.tracer.trace("t0") { ctx =>
          ctx.withSegment("s0") {
          }
          ctx.withSegment("s1") {
          }
          ctx.withSegment("s2") {
          }
        }
        prompt()
      case _ =>
        prompt()
    }
  }
}
