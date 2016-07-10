package io.treev.common.test

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}

import scala.concurrent.duration._
import scala.concurrent.{Await, Awaitable, Future}

trait PatienceConfig {
  self: ScalaFutures =>

  protected val defaultPatience = PatienceConfig(timeout = Span(PatienceSeconds.toSeconds, Seconds))

  protected def await[T](awaitable: Awaitable[T], duration: Duration = PatienceSeconds): T = {
    Await.result(awaitable, duration)
  }

  protected implicit def awaiter[T]: Future[T] => T = t => await(t)

  private lazy val PatienceSeconds: Duration = 1.second

}
