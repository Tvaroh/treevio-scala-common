package io.treev.common.api

import cats.implicits._
import io.treev.common.concurrent.Async

import scala.concurrent.ExecutionContext

trait Lifecycle {

  def start()(implicit ec: ExecutionContext): Async[Unit] = ().pure[Async]
  def stop()(implicit ec: ExecutionContext): Async[Unit] = ().pure[Async]

}
