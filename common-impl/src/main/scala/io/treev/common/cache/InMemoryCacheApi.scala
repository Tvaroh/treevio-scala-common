package io.treev.common.cache

import io.treev.common.api.Api

trait InMemoryCacheApi[M[_]] extends Api[M] {

  def caching[T](keyParts: Any*)(f: => M[T]): Single[T]

}
