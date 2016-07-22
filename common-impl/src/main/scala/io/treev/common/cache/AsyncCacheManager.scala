package io.treev.common.cache

import io.treev.common.concurrent.Async

import scala.concurrent.ExecutionContext
import scalacache._

class AsyncCacheManager(inMemoryCacheBackend: Cache[NoSerialization])
                       (implicit ec: ExecutionContext) extends CacheManager[Async] {

  override def createInMemoryCache(): InMemoryCacheApi[Async] = {
    val scalaCache = ScalaCache(inMemoryCacheBackend)
    new AsyncInMemoryCacheApi(scalaCache)
  }

}
