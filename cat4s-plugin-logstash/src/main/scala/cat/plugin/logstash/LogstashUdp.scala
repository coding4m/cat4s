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

import java.net.{ DatagramPacket, DatagramSocket, InetSocketAddress }

/**
 * @author siuming
 */
class LogstashUdp(host: String, port: Int) extends LogstashDestination {
  val address = new InetSocketAddress(host, port)
  val socket = new DatagramSocket()

  override def send(bytes: Array[Byte]): Unit = {
    val packet = new DatagramPacket(bytes, 0, bytes.length, address)
    socket.send(packet)
  }

  override def close(): Unit = {
    if (null != socket) {
      try {
        socket.close()
      } catch {
        case _: Throwable => //
      }
    }
  }
}
