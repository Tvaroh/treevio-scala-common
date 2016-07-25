package io.treev.common.cache

import monix.eval.Task

trait InMemoryCacheApi {

  def caching[T](keyParts: Any*)(f: => Task[T]): Task[T]

}
