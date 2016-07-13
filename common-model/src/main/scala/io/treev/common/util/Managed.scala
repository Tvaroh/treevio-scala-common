package io.treev.common.util

import cats.syntax.functor._
import io.treev.common.concurrent.Async

import scala.concurrent.{ExecutionContext, Future}

object Managed {

  type AutoCloseableView[T] = T => AutoCloseable

  def apply[T : AutoCloseableView, V](resource: T)(op: T => V): V =
    try {
      op(resource)
    } finally {
      resource.close()
    }

  def future[T : AutoCloseableView, V](resource: T)(op: T => Future[V])
                                      (implicit ec: ExecutionContext): Future[V] = {
    val future = op(resource)
    future.onComplete(_ => resource.close())
    future
  }

  def async[T : AutoCloseableView, V](resource: T)(op: T => Async[V])
                                     (implicit ec: ExecutionContext): Async[V] =
    op(resource).map(v => try v finally { resource.close() })

}
