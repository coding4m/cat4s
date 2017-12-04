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

import java.util.UUID

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

/**
 * @author siuming
 */
object Trace {
  val DefaultHandler = new TraceHandler {
    override def resolve(cause: Throwable) = TraceStatus(-1, cause.getMessage)
  }

  private case class DefaultContext(
    traceId: String,
    parentId: Option[String],
    id: String,
    name: String,
    tags: Seq[String],
    data: Map[String, String],
    source: TraceSource,
    handler: TraceHandler) extends TraceContext {
    @volatile private var _status: TraceStatus = _
    @volatile private var _clock: TraceClock = TraceClock(startNano = System.nanoTime(), elapsedNano = -1L)
    @volatile private var _segments: Seq[TraceSegment] = Seq.empty
    override val status = _status
    override val clock = _clock
    override def complete(status: TraceStatus) = {
      assert(!isCompleted, "context has been completed.")
      assert(_segments.forall(_.isCompleted), "segments must all completed.")
      this._status = status
      this._clock = this._clock.copy(elapsedNano = System.nanoTime() - this._clock.startNano)
    }
    override def newSegment(name: String, data: Map[String, String]): TraceSegment = {
      val segment = DefaultSegment(name, data, handler)
      this._segments = this._segments :+ segment
      segment
    }
  }

  private case class DefaultSegment(name: String, data: Map[String, String], handler: TraceHandler) extends TraceSegment {
    @volatile private var _status: TraceStatus = _
    @volatile private var _clock: TraceClock = TraceClock(startNano = System.nanoTime(), elapsedNano = -1L)
    override val status = _status
    override val clock = _clock
    override def complete(status: TraceStatus) = {
      assert(!isCompleted, "segment has been completed.")
      this._status = status
      this._clock = this._clock.copy(elapsedNano = System.nanoTime() - this._clock.startNano)
    }

    override def collect[T](f: => T) = {
      try {
        val result = f
        complete(TraceStatus.OkStatus)
        result
      } catch {
        case e: Throwable =>
          complete(handler.resolve(e))
          throw e
      }
    }

    override def collect[T](f: => Future[T])(implicit ec: ExecutionContext) = {
      f.andThen {
        case Success(_) => complete(TraceStatus.OkStatus)
        case Failure(e) => complete(handler.resolve(e))
      }
    }
  }
}
class Trace private[trace] (name: String, source: TraceSource) {
  import Trace._

  private var traceId: Option[String] = None
  private var parentId: Option[String] = None
  private var id: Option[String] = None
  private var tags: Seq[String] = Seq.empty
  private var data: Map[String, String] = Map.empty
  private var handler = DefaultHandler

  def withTraceId(traceId: Option[String]): Trace = {
    this.traceId = traceId
    this
  }

  def withParentId(parentId: Option[String]): Trace = {
    this.parentId = parentId
    this
  }

  def withId(id: Option[String]): Trace = {
    this.id = id
    this
  }

  def withTag(tag: String): Trace = {
    this.tags = this.tags :+ tag
    this
  }

  def withTags(tags: Seq[String]): Trace = {
    this.tags = this.tags ++ tags
    this
  }

  def withData(name: String, value: String): Trace = {
    data = data + (name -> value)
    this
  }

  def withDatas(tags: Map[String, String]): Trace = {
    this.data = this.data ++ tags
    this
  }

  def withHandler(handler: TraceHandler): Trace = {
    this.handler = handler
    this
  }

  def withHandler(pf: PartialFunction[Throwable, TraceStatus]): Trace = {
    this.handler = new TraceHandler {
      override def resolve(cause: Throwable) = if (pf.isDefinedAt(cause)) pf(cause) else DefaultHandler.resolve(cause)
    }
    this
  }

  def apply[T](f: TraceContext => T): T = collect(f)

  def collect[T](f: TraceContext => T): T = {
    val ctx = buildContext()
    try {
      val result = f(ctx)
      ctx.complete(TraceStatus.OkStatus)
      result
    } catch {
      case e: Throwable =>
        ctx.complete(handler.resolve(e))
        throw e
    }
  }

  def apply[T](f: TraceContext => Future[T])(implicit ec: ExecutionContext): Future[T] = collect(f)

  def collect[T](f: TraceContext => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    val ctx = buildContext()
    f(ctx).andThen {
      case Success(_) => ctx.complete(TraceStatus.OkStatus)
      case Failure(e) => ctx.complete(handler.resolve(e))
    }
  }

  private def buildContext(): TraceContext = DefaultContext(
    traceId.getOrElse(UUID.randomUUID().toString),
    parentId,
    id.getOrElse(UUID.randomUUID().toString),
    name,
    tags,
    data,
    source,
    handler
  )
}
