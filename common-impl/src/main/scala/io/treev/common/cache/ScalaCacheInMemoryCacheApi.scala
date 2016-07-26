package io.treev.common.cache

import monix.eval.Task
import monix.execution.Scheduler

import scalacache._

class ScalaCacheInMemoryCacheApi(scalaCacheBackend: ScalaCache[NoSerialization])
                                (implicit scheduler: Scheduler) extends InMemoryCacheApi {

  override def caching[T](keyParts: Any*)(f: => Task[T]): Task[T] =
    Task.defer {
      Task.fromFuture {
        scalacache.caching(keyParts: _*)(f.runAsync)
      }
    }

  private implicit val _scalaCacheBackend: ScalaCache[NoSerialization] = scalaCacheBackend

}
