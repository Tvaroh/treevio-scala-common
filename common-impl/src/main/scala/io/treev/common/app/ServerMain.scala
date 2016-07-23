package io.treev.common.app

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.treev.common.api.Lifecycle
import io.treev.common.concurrent.Async
import io.treev.common.logging.Logger

import scala.concurrent.ExecutionContext

abstract class ServerMain(serverConfiguration: ServerConfiguration)
                         (implicit actorSystem: ActorSystem, materializer: Materializer)
  extends App
    with Lifecycle {

  def route: Route

  import serverConfiguration._

  override def start()(implicit ec: ExecutionContext): Async[Unit] =
    Async { () =>
      logger.info(s"Starting $serverId server...")

      val binding = Http().bindAndHandle(route, host, port)

      binding.foreach { _ =>
        logger.info(s"$serverId server started on $host:$port")
      }

      binding.map(_ => ())
    }

  private val logger = Logger[this.type]

}
