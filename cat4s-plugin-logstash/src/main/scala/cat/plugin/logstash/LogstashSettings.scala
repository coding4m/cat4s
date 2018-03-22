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

import java.util.concurrent.TimeUnit

import com.typesafe.config.Config

/**
 * @author siuming
 */
class LogstashSettings(config: Config) {
  val serviceName =
    config.getString("cat.plugin.logstash.service-name")

  val serviceHost =
    config.getString("cat.plugin.logstash.service-host")

  val servicePort =
    config.getString("cat.plugin.logstash.service-port")

  val selector = config.getString("cat.plugin.logstash.destination-selector").toLowerCase match {
    case RoundRobinSelector.Name => RoundRobinSelector
    case _                       => RandomSelector
  }

  val destinations = {
    import LogstashDestination._
    config.getString("cat.plugin.logstash.destinations").split(HostSeparator) collect {
      case HostAndPort(host, port) => new LogstashUdp(host, port.toInt)
    }
  }

  val maxBufferSize =
    config.getInt("cat.plugin.logstash.max-buffer-size")

  val maxRetries =
    config.getInt("cat.plugin.logstash.max-retries")

  val maxFails =
    config.getInt("cat.plugin.logstash.max-fails")

  val failTimeout =
    config.getDuration("cat.plugin.logstash.fail-timeout", TimeUnit.MILLISECONDS)

}
