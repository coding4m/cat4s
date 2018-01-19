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

import akka.actor.{ Actor, ActorRef, Props, Stash, Terminated }

/**
 * @author siuming
 */
private[trace] object SubscriptionController {
  val Name = "trace-subscriptions"
  def props(): Props =
    Props(new SubscriptionController)

  case object Process
}
private[trace] class SubscriptionController extends Actor with Stash {
  import SubscriptionProtocol._
  import SubscriptionController._

  private var subscribers = Seq.empty[ActorRef]
  override def receive = initiating.orElse(terminated)

  private def initiating: Receive = {
    case Process =>
      context become initiated.orElse(terminated)
      unstashAll()
    case Subscribe(s) => if (!subscribers.contains(s)) {
      subscribers = subscribers :+ context.watch(s)
    }
    case Unsubscribe(s) =>
      subscribers = subscribers.filterNot(_ == context.unwatch(s))
    case _ =>
      stash()
  }

  private def initiated: Receive = {
    case Subscribe(s) => if (!subscribers.contains(s)) {
      subscribers = subscribers :+ context.watch(s)
    }
    case Unsubscribe(s) =>
      subscribers = subscribers.filterNot(_ == context.unwatch(s))
    case info: TraceInfo =>
      subscribers.foreach(_ ! info)
  }

  private def terminated: Receive = {
    case Terminated(s) => subscribers = subscribers.filterNot(_ == s)
  }
}
