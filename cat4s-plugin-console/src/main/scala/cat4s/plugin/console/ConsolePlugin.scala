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

package cat4s.plugin.console

import akka.actor.{ ExtendedActorSystem, ExtensionId, ExtensionIdProvider }
import cat4s.{ Cat, Plugin }

/**
 * @author siuming
 */
object ConsolePlugin extends ExtensionId[ConsolePlugin] with ExtensionIdProvider {
  override def lookup() = ConsolePlugin
  override def createExtension(system: ExtendedActorSystem) = new ConsolePlugin(system)
}
class ConsolePlugin(system: ExtendedActorSystem) extends Plugin {
  val reporter = system.actorOf(ConsoleReporter.props(), ConsoleReporter.Name)
  Cat.tracer.subscribe(reporter)
  Cat.metrics.subscribe(reporter)
}
