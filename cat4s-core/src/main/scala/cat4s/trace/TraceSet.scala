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
  def trace(name: String, source: TraceSource): Trace
  def trace(name: String, host: String, port: Int): Trace = trace(name, TraceSource(host, port))
  def withContext[T](name: String, source: TraceSource)(f: TraceContext => T): T = withContext(name, source, None)(f)
  def withContext[T](name: String, source: TraceSource, traceId: Option[String])(f: TraceContext => T): T = withContext(name, source, traceId, None)(f)
  def withContext[T](name: String, source: TraceSource, traceId: Option[String], parentId: Option[String])(f: TraceContext => T): T = trace(name, source).withTraceId(traceId).withParentId(parentId).apply(f)
  def withContext[T](name: String, host: String, port: Int)(f: TraceContext => T): T = withContext(name, host, port, None)(f)
  def withContext[T](name: String, host: String, port: Int, traceId: Option[String])(f: TraceContext => T): T = withContext(name, TraceSource(host, port), traceId, None)(f)
  def withContext[T](name: String, host: String, port: Int, traceId: Option[String], parentId: Option[String])(f: TraceContext => T): T = withContext(name, TraceSource(host, port), traceId, parentId)(f)
  def withAsyncContext[T](name: String, source: TraceSource)(f: TraceContext => Future[T])(implicit ec: ExecutionContext): Future[T] = withAsyncContext(name, source, None, None)(f)
  def withAsyncContext[T](name: String, source: TraceSource, traceId: Option[String])(f: TraceContext => Future[T])(implicit ec: ExecutionContext): Future[T] = withAsyncContext(name, source, traceId, None)(f)
  def withAsyncContext[T](name: String, source: TraceSource, traceId: Option[String], parentId: Option[String])(f: TraceContext => Future[T])(implicit ec: ExecutionContext): Future[T] = trace(name, source).withTraceId(traceId).withParentId(parentId).apply(f)
  def withAsyncContext[T](name: String, host: String, port: Int)(f: TraceContext => Future[T])(implicit ec: ExecutionContext): Future[T] = withAsyncContext(name, host, port, None, None)(f)
  def withAsyncContext[T](name: String, host: String, port: Int, traceId: Option[String])(f: TraceContext => Future[T])(implicit ec: ExecutionContext): Future[T] = withAsyncContext(name, host, port, traceId, None)(f)
  def withAsyncContext[T](name: String, host: String, port: Int, traceId: Option[String], parentId: Option[String])(f: TraceContext => Future[T])(implicit ec: ExecutionContext): Future[T] = withAsyncContext(name, TraceSource(host, port), traceId, parentId)(f)

  def subscribe(subscriber: ActorRef): Unit
  def unsubscribe(subscriber: ActorRef): Unit

  private[cat4s] def start(): Unit
  private[cat4s] def stop(): Unit
}
