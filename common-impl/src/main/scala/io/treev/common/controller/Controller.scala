package io.treev.common.controller

import akka.http.scaladsl.server._

trait Controller
  extends JsonEncoders {

  def route: Route

}
