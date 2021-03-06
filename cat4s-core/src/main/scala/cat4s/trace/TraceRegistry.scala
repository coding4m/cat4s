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
import akka.actor.{ ActorRef, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }

/**
 * @author siuming
 */
object TraceRegistry extends ExtensionId[TraceRegistry] with ExtensionIdProvider {
  override def lookup() = TraceRegistry
  override def createExtension(system: ExtendedActorSystem) = new TraceRegistry(system)
}
class TraceRegistry(system: ExtendedActorSystem) extends Extension with TraceSet {
  import SubscriptionProtocol._
  import SubscriptionController._
  private val controller = system.actorOf(SubscriptionController.props(), SubscriptionController.Name)

  override def trace(name: String): Trace = new Trace(name, controller)
  override def subscribe(subscriber: ActorRef): Unit = controller ! Subscribe(subscriber)
  override def unsubscribe(subscriber: ActorRef): Unit = controller ! Unsubscribe(subscriber)
  override private[cat4s] def start(): Unit = controller ! Process
  override private[cat4s] def stop(): Unit = {}
}
