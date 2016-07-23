package io.treev.common.app.http

trait HttpServerConfiguration {
  def serverId: String
  def interface: String
  def port: Int
}

object HttpServerConfiguration {
  def apply(_serverId: String,
            _interface: String,
            _port: Int): HttpServerConfiguration =
    new HttpServerConfiguration {
      override def serverId: String = _serverId
      override def interface: String = _interface
      override def port: Int = _port
    }
}
