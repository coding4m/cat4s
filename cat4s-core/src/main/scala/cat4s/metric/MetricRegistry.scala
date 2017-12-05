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
import akka.actor.{ ActorRef, ExtendedActorSystem, Extension }

/**
 * @author siuming
 */
class MetricRegistry(system: ExtendedActorSystem) extends Extension with MetricSet {
  private val settings = new MetricSettings(system.settings.config)

  override def collect(ctx: InstrumentContext) = ???
  override def subscribe(subscriber: ActorRef, filter: SubscriptionFilter, permanently: Boolean) = ???
  override def unsubscribe(subscriber: ActorRef) = ???
  override private[cat4s] def start() = ???
  override private[cat4s] def stop() = ???
}
