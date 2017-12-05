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

import cat4s.metric.MetricRegistry
import cat4s.trace.TraceRegistry

import scala.concurrent.Future

/**
 * @author siuming
 */
object Cat {
  import scala.concurrent.ExecutionContext.Implicits._
  val tracer = ???
  val metrics = ???
  def start(): Unit = {
    val metrics = new MetricRegistry(null)
    val tracer = new TraceRegistry(null)
    metrics.start()
    tracer.withContext("", null) { ctx =>
      ctx.withSegment("") {
        ???
      }

      ctx.withAsyncSegment("") {
        ???
      }
      ???
    }
    tracer.withAsyncContext("", null) { ctx =>
      ???
    }
    tracer
      .trace("", null)
      .withId(Some(""))
      .withTag("")
      .withData("", "")
      .withData("name", "value")
      .collect { ctx =>
        ???
      }

    tracer
      .trace("", null)
      .withTraceId(Some(""))
      .withId(Some(""))
      .withTag("")
      .withData("", "")
      .collectAsync { ctx =>
        ctx.withSegment("s0") {
          "s0"
        }
        ctx.withAsyncSegment("s1") {
          Future.successful("s1")
        } flatMap { _ =>
          ctx.withAsyncSegment("s2") {
            Future.successful("s2")
          }
        }
      }
  }
  def stop(): Unit = ???
}
