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

import java.io.Closeable
import java.nio.charset.Charset
import java.util.concurrent.Executors

import cat.plugin.logstash.LogstashPublisher.{ EventObject, EventObjectFactory, EventObjectTranslator }
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.{ EventFactory, EventHandler, EventTranslatorOneArg, ExceptionHandler }
import org.json4s.Extraction
import org.json4s.jackson.JsonMethods.compact

/**
 * @author siuming
 */
object LogstashPublisher {
  private[logstash] class EventObject {
    var value: AnyRef = _
    def set(value: AnyRef): Unit = this.value = value
  }

  private[logstash] class EventObjectFactory extends EventFactory[EventObject] {
    override def newInstance() = new EventObject
  }

  private[logstash] class EventObjectTranslator extends EventTranslatorOneArg[EventObject, AnyRef] {
    override def translateTo(event: EventObject, sequence: Long, arg0: AnyRef): Unit = event.set(arg0)
  }
}
class LogstashPublisher(
  destinations: Seq[LogstashDestination],
  selector: LogstashSelector,
  maxBufferSize: Int,
  maxRetries: Int,
  maxFails: Int,
  failTimeout: Long) extends Closeable {

  private case class BackupHint(id: String, fails: Int)
  private case class BackoffHint(destination: LogstashDestination, until: Long)

  @volatile private var hints = Seq.empty[BackupHint]
  @volatile private var backups = Seq.empty[BackoffHint]
  @volatile private var actives = Seq(destinations: _*)
  @volatile private var closed: Boolean = false

  private implicit val formats = org.json4s.DefaultFormats
  private val charset = Charset.forName("utf-8")
  private val factory = new EventObjectFactory
  private val translator = new EventObjectTranslator
  private val disruptor = new Disruptor(factory, maxBufferSize, Executors.defaultThreadFactory())
  disruptor.handleEventsWith(new EventHandler[EventObject] {
    override def onEvent(event: EventObject, sequence: Long, endOfBatch: Boolean): Unit = {
      send(compact(Extraction.decompose(event.value).snakizeKeys))
    }
  })
  disruptor.setDefaultExceptionHandler(new ExceptionHandler[EventObject] {
    override def handleOnStartException(ex: Throwable): Unit = throw ex
    override def handleOnShutdownException(ex: Throwable): Unit = throw ex
    override def handleEventException(ex: Throwable, sequence: Long, event: EventObject): Unit = {}
  })
  disruptor.start()

  def publish(event: AnyRef): Unit = {
    disruptor.getRingBuffer.publishEvent(translator, event)
  }

  private def send(content: String): Unit = {
    if (closed) {
      throw new IllegalStateException("router already closed.")
    }
    sendAndRetry(maxRetries, content.getBytes(charset))
  }

  private def sendAndRetry(retryTimes: Int, bytes: Array[Byte]): Unit = {
    upgradeDestination()
    val d = selectDestination()
    try {
      d.send(bytes)
    } catch {
      case e: Throwable =>
        downgradeDestination(d)
        if (retryTimes <= 1) {
          throw e
        }
        sendAndRetry(retryTimes - 1, bytes)
    }
  }

  private def selectDestination() = {
    val _actives = actives
    if (_actives.isEmpty) {
      throw new LogstashNotFoundException
    }
    selector.select(_actives)
  }

  private def upgradeDestination(): Unit = {
    val _backups = backups
    if (_backups.nonEmpty) {
      this.synchronized {
        backups.filter(_.until <= System.currentTimeMillis()).foreach { it =>
          backups = backups.filterNot(_.destination.id != it.destination.id)
          actives = actives.filterNot(_.id != it.destination.id) :+ it.destination
        }
      }
    }
  }

  private def downgradeDestination(destination: LogstashDestination): Unit = {
    this.synchronized {
      val hint = hints.find(_.id == destination.id).getOrElse(BackupHint(destination.id, 0))
      if (hint.fails >= maxFails - 1) {
        backups = backups.filterNot(_.destination.id != hint.id) :+ BackoffHint(destination, System.currentTimeMillis() + failTimeout)
        actives = actives.filterNot(_.id != hint.id)
        hints = hints.filterNot(_.id != hint.id)
      } else {
        hints = hints.filterNot(_.id != hint.id) :+ hint.copy(fails = hint.fails + 1)
      }
    }
  }

  override def close(): Unit = {
    this.synchronized {
      closed = true
      destinations.foreach(_.close())
    }
  }
}
