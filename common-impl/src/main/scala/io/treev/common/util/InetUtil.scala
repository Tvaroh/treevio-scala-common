package io.treev.common.util

import com.datastax.driver.core.ProtocolOptions

object InetUtil {

  def splitToHostAndPort(hostWithPort: String, defaultPort: => Int): (String, Int) =
    hostWithPort.split(':') match {
      case Array(host) => (host, ProtocolOptions.DEFAULT_PORT)
      case Array(host, port) => (host, port.toInt)
    }

}
