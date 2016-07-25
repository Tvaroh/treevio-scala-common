package io.treev.common.cache

trait CacheManager {

  def createInMemoryCache(): InMemoryCacheApi

}
