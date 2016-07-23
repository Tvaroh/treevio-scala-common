package io.treev.common.app.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import cats.implicits._
import io.treev.common.api.Lifecycle
import io.treev.common.concurrent.Async
import io.treev.common.logging.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

abstract class HttpServerMain(httpServerConfiguration: HttpServerConfiguration)
                             (implicit actorSystem: ActorSystem, materializer: Materializer)
  extends App
    with Lifecycle {

  def route: Route
  def init(): Async[Unit] = Async.unit

  import httpServerConfiguration._

  override def start()(implicit ec: ExecutionContext): Async[Unit] =
    for {
      _ <- init()
      _ <- Async { () =>
        logger.info(s"Starting $serverId server...")

        val binding = Http().bindAndHandle(route, interface, port)

        binding.onComplete {
          case Success(_) =>
            logger.info(s"$serverId server started on $interface:$port")
          case Failure(t) =>
            logger.error(s"$serverId server failed to start on $interface:$port")
        }

        binding
      }
    } yield ()

  private val logger = Logger[this.type]

}
