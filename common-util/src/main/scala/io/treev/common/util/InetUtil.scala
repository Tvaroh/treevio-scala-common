package io.treev.common.util

object InetUtil {

  def splitToHostAndPort(hostWithPort: String, defaultPort: => Int): (String, Int) =
    hostWithPort.split(':') match {
      case Array(host) => (host, defaultPort)
      case Array(host, port) => (host, port.toInt)
    }

}
