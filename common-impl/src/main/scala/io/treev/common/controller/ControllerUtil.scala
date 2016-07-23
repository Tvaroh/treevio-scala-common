package io.treev.common.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait ControllerUtil {

  def wireControllers(prefix: Option[String] = None)
                     (controllers: Controller*): Route = {
    val route = controllers map (_.route) reduce (_ ~ _)
    prefix.fold(route)(pathPrefix(_)(route))
  }

}

object ControllerUtil extends ControllerUtil
