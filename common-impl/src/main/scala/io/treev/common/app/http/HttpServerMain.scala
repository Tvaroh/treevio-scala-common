package io.treev.common.app.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.treev.common.api.Lifecycle
import io.treev.common.logging.Log
import monix.eval.Task
import monix.execution.Scheduler

import scala.util.{Failure, Success}

abstract class HttpServerMain(httpServerConfiguration: HttpServerConfiguration)
                             (implicit actorSystem: ActorSystem, materializer: Materializer)
  extends App
    with Lifecycle {

  protected def route: Route
  protected def init(): Task[Unit] = Task.unit

  import httpServerConfiguration._

  override def start()(implicit scheduler: Scheduler): Task[Unit] =
    for {
      _ <- Task.defer {
        log.info(s"Initializing $serverId server...")
        init()
      }
      _ <- Task.fromFuture {
        log.info(s"Starting $serverId HTTP server...")

        val binding = Http().bindAndHandle(route, interface, port)

        binding.onComplete {
          case Success(_) =>
            log.info(s"$serverId server started on $interface:$port")
          case Failure(t) =>
            log.error(s"$serverId server failed to start on $interface:$port", t)
        }

        binding
      }
    } yield ()

  private val log = Log[this.type]

}
