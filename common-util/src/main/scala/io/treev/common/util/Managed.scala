package io.treev.common.util

import scala.concurrent.{ExecutionContext, Future}

object Managed {

  type AutoCloseableView[T] = T => AutoCloseable

  def apply[T: AutoCloseableView, V](resource: T)(op: T => V): V =
    try {
      op(resource)
    } finally {
      resource.close()
    }

  def future[T : AutoCloseableView, V](resource: T)(op: T => Future[V])
                                      (implicit executionContext: ExecutionContext): Future[V] =
    op(resource).andThen { case _ => resource.close() }

}
