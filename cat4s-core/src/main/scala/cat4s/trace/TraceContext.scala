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

package cat4s.trace

import scala.concurrent.{ ExecutionContext, Future }

/**
 * @author siuming
 */
trait TraceContext {
  def traceId: String
  def parentId: Option[String]
  def id: String
  def name: String
  def tags: Seq[String]
  def data: Map[String, String]
  def clock: TraceClock
  def status: TraceStatus
  def source: TraceSource

  def isSuccess: Boolean = isCompleted && status.status == TraceStatus.Ok
  def isCompleted: Boolean = null != status
  def complete(status: TraceStatus): Unit

  def newSegment(name: String): Segment = newSegment(name, Map.empty[String, String])
  def newSegment(name: String, data: Map[String, String]): Segment
  def withSegment[T](name: String)(f: => T): T = withSegment(name, Map.empty)(f)
  def withSegment[T](name: String, data: Map[String, String])(f: => T): T = newSegment(name, data).collect(f)
  def withAsyncSegment[T](name: String)(f: => Future[T])(implicit ec: ExecutionContext): Future[T] = withAsyncSegment(name, Map.empty)(f)
  def withAsyncSegment[T](name: String, data: Map[String, String])(f: => Future[T])(implicit ec: ExecutionContext): Future[T] = newSegment(name, data).collect(f)
}
