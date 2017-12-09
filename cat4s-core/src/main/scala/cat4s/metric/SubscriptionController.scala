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

package cat4s.metric

import akka.actor.{ Actor, ActorRef, Props }

/**
 * @author siuming
 */
private[metric] object SubscriptionController {
  val Name = "metrics-subscription-controller"
  def props(): Props =
    Props(new SubscriptionController)
  case object Process
}
private[metric] class SubscriptionController extends Actor {
  private var subscribers = Seq.empty[ActorRef]
  override def receive = initiating

  private def initiating: Receive = {
    //todo
    case msg => println(msg)
  }

  private def initiated: Receive = {
    ???
  }
}
