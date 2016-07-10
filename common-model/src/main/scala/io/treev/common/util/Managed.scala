package io.treev.common.util

object Managed {

  type AutoCloseableView[T] = T => AutoCloseable

  def apply[T : AutoCloseableView, V](resource: T)(op: T => V): V =
    try {
      op(resource)
    } finally {
      resource.close()
    }

}
