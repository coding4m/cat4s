package cat4s.plugin.jmx

import akka.actor.{ExtendedActorSystem, ExtensionId, ExtensionIdProvider}
import cat4s.{Cat, Plugin}

/**
  * @author siuming
  */
object JmxPlugin extends ExtensionId[JmxPlugin] with ExtensionIdProvider{
  override def lookup() = JmxPlugin
  override def createExtension(system: ExtendedActorSystem) = new JmxPlugin(system)
}
class JmxPlugin(system: ExtendedActorSystem) extends Plugin{
  system.actorOf(JmxCollector.props(Cat.metrics.sample(JmxMetrics,"metrics")))
}
