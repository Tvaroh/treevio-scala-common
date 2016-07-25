package io.treev.common.api

import monix.eval.Task
import monix.execution.Scheduler

trait Lifecycle {

  def start()(implicit scheduler: Scheduler): Task[Unit] = Task.unit
  def stop()(implicit scheduler: Scheduler): Task[Unit] = Task.unit

}
