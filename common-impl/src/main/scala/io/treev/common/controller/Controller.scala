package io.treev.common.controller

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import io.treev.common.exception.ApiException
import io.treev.common.logging.Log
import monix.eval.Task
import monix.execution.Scheduler

import scala.util.control.NonFatal
import scala.util.{Failure, Success}

trait Controller
  extends JsonEncoders
    with Completions {

  def route: Route

  protected def handleErrors[T](operation: Task[T])
                               (f: T => ToResponseMarshallable)
                               (implicit scheduler: Scheduler): Route =
    onComplete[T](operation.runAsync) {
      case Success(value) => complete(f(value))
      case Failure(NonFatal(t)) =>
        t match {
          case e: ApiException =>
            log.error("Operation failed due to API exception", t)
            completeInternalError(e)
          case _ =>
            log.error("Operation failed due to an unexpected exception", t)
            complete(StatusCodes.InternalServerError)
        }
    }

  private val log = Log[this.type]

}
