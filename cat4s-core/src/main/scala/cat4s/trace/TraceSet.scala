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

package cat4s.trace

import akka.actor.ActorRef

import scala.concurrent.{ ExecutionContext, Future }

/**
 * @author siuming
 */
trait TraceSet {

  def trace(name: String): Trace
  def withContext[T](name: String)(f: TraceContext => T): T = withContext(name, None)(f)
  def withContext[T](name: String, traceId: Option[String])(f: TraceContext => T): T = withContext(name, traceId, None)(f)
  def withContext[T](name: String, traceId: Option[String], parentId: Option[String])(f: TraceContext => T): T = trace(name).withTraceId(traceId).withParentId(parentId)(f)
  def withAsyncContext[T](name: String)(f: TraceContext => Future[T])(implicit ec: ExecutionContext): Future[T] = withAsyncContext(name, None)(f)
  def withAsyncContext[T](name: String, traceId: Option[String])(f: TraceContext => Future[T])(implicit ec: ExecutionContext): Future[T] = withAsyncContext(name, traceId, None)(f)
  def withAsyncContext[T](name: String, traceId: Option[String], parentId: Option[String])(f: TraceContext => Future[T])(implicit ec: ExecutionContext): Future[T] = trace(name).withTraceId(traceId).withParentId(parentId).collect(f)

  def subscribe(subscriber: ActorRef): Unit
  def unsubscribe(subscriber: ActorRef): Unit

  private[cat4s] def start(): Unit
  private[cat4s] def stop(): Unit
}
