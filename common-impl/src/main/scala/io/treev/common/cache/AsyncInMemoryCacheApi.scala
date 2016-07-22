package io.treev.common.cache

import io.treev.common.concurrent.Async

import scala.concurrent.ExecutionContext
import scalacache._

class AsyncInMemoryCacheApi(scalaCacheBackend: ScalaCache[NoSerialization])
                           (implicit ec: ExecutionContext) extends InMemoryCacheApi[Async] {

  override def caching[T](keyParts: Any*)(f: => Async[T]): Single[T] =
    Async { () =>
      scalacache.caching(keyParts: _*)(f.run())
    }

  private implicit val _scalaCacheBackend: ScalaCache[NoSerialization] = scalaCacheBackend

}
