package io.treev.common.api

import io.treev.common.concurrent.Async

import scala.concurrent.ExecutionContext

trait Lifecycle {

  def start(implicit ec: ExecutionContext): Async[Unit] = Async.unit
  def stop(implicit ec: ExecutionContext): Async[Unit] = Async.unit

}
