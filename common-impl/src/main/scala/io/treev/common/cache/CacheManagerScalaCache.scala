package io.treev.common.cache

import monix.execution.Scheduler

import scalacache._

class CacheManagerScalaCache(inMemoryCacheBackend: Cache[NoSerialization])
                            (implicit scheduler: Scheduler) extends CacheManager {

  override def createInMemoryCache(): InMemoryCacheApi = {
    val scalaCache = ScalaCache(inMemoryCacheBackend)
    new InMemoryCacheApiScalaCache(scalaCache)
  }

}
