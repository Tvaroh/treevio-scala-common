package io.treev.common.util

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.{ExecutionContext, Future}

object Managed {

  type AutoCloseableView[T] = T => AutoCloseable

  def apply[T: AutoCloseableView, V](resource: T)(op: T => V): V =
    try {
      op(resource)
    } finally {
      resource.close()
    }

  def future[T: AutoCloseableView, V](resource: T)(op: T => Future[V])
                                     (implicit ec: ExecutionContext): Future[V] = {
    val future = op(resource)
    future.onComplete(_ => resource.close())
    future
  }

  def task[T: AutoCloseableView, V](resource: T)(op: T => Task[V])
                                   (implicit scheduler: Scheduler): Task[V] =
    op(resource).map(v => try v finally { resource.close() })

}
