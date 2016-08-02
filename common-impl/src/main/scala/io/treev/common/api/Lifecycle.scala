package io.treev.common.api

import monix.eval.Task

trait Lifecycle {

  def start(): Task[Unit] = Task.unit
  def stop(): Task[Unit] = Task.unit

}
