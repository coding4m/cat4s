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
trait TraceSegment {
  def name: String
  def data: Map[String, String]
  def clock: TraceClock
  def status: TraceStatus

  def isSuccess: Boolean = isCompleted && status.status == TraceStatus.Ok
  def isCompleted: Boolean = null != status
  def complete(status: TraceStatus): Unit

  def apply[T](f: => T): T = collect(f)
  def apply[T](f: => Future[T])(implicit ec: ExecutionContext): Future[T] = collect(f)
  def collect[T](f: => T): T
  def collect[T](f: => Future[T])(implicit ec: ExecutionContext): Future[T]
}
