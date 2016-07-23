package io.treev.common.app.http

trait HttpServerConfiguration {
  def serverId: String
  def interface: String
  def port: Int
}
