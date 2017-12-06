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

package cat4s.metric
import akka.actor.{ ActorRef, Cancellable, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }

/**
 * @author siuming
 */
object MetricRegistry extends ExtensionId[MetricRegistry] with ExtensionIdProvider {
  override def lookup() = MetricRegistry
  override def createExtension(system: ExtendedActorSystem) = new MetricRegistry(system)
}
class MetricRegistry(system: ExtendedActorSystem) extends Extension with MetricSet {
  import SubscriptionProtocol._
  import SubscriptionController._

  private val settings = new MetricSettings(system.settings.config)
  private val controller = system.actorOf(SubscriptionController.props(), SubscriptionController.Name)

  @volatile private var scheduler: Option[Cancellable] = None

  override def subscribe(subscriber: ActorRef, filter: MetricFilter, permanently: Boolean) = controller ! Subscribe(subscriber, filter, permanently)
  override def unsubscribe(subscriber: ActorRef) = controller ! Unsubscribe(subscriber)

  override private[cat4s] def start() = {
    import system.dispatcher
    //todo
    scheduler.foreach(_.cancel())
    scheduler = Some(system.scheduler.schedule(null, null) {
      //collect
      controller ! MetricSample(Map.empty)
    })

    controller ! Process
  }
  override private[cat4s] def stop() = scheduler.foreach(_.cancel())
}
