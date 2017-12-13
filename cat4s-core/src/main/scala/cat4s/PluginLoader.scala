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

package cat4s

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.Config
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.{Around, Aspect, Pointcut}

/**
 * @author siuming
 */
private[cat4s] object PluginLoader extends ExtensionId[PluginLoader] with ExtensionIdProvider {
  override def lookup() = PluginLoader
  override def createExtension(system: ExtendedActorSystem) = new PluginLoader(system)
}
private[cat4s] class PluginLoader(system: ExtendedActorSystem) extends Extension {
  val settings = new PluginSettings(system.settings.config)

  if (settings.aspectJWarning && settings.aspectJPlugins.nonEmpty && !isAspectJPresent) {
    //warning.. todo
  }

  // load plugins.
  settings.availablePlugins.foreach { plugin =>
    system
      .dynamicAccess
      .getObjectFor[ExtensionId[Plugin]](plugin.entryPoint)
      .map(_.get(system))
      .recover {
        case _: Throwable => // log it. todo
      }
  }
  def isAspectJPresent: Boolean = false
}
@Aspect
private[cat4s] class PluginAspect {
  @Pointcut("execution(* cat4s.PluginLoader.isAspectJPresent())")
  def aspectJPresent(): Unit = {}
  @Around("aspectJPresent()")
  def aspectJEnabled(pjp: ProceedingJoinPoint): Boolean = true
}
private[cat4s] class PluginSettings(config: Config) {
  import scala.collection.JavaConverters._

  val enablePlugins: Seq[String] =
    config.getStringList("cat.enable-plugins").asScala

  val enableAllPlugins: Boolean =
    config.getBoolean("cat.enable-all-plugins")

  val availablePlugins: Set[PluginInfo] = {
    val plugins = config
      .getConfig("cat.plugin")
      .entrySet().asScala
      .map(entry => entry.getKey.takeWhile(_ != '.'))
      .toSet
    if (enableAllPlugins) plugins.map(loadPluginInfo(config, _)) else plugins.filter(enablePlugins.contains).map(loadPluginInfo(config, _))
  }

  val aspectJError: Boolean =
    config.getBoolean("cat.aspectj-error")

  val aspectJWarning: Boolean =
    config.getBoolean("cat.aspectj-warning")

  val aspectJPlugins =
    availablePlugins.filter(_.aspectJRequired)

  private def loadPluginInfo(config: Config, name: String): PluginInfo = {
    val conf = config.getConfig(s"cat.plugin.$name")
    PluginInfo(name, conf.getBoolean("aspectj-required"), conf.getString("entry-point"))
  }
}
