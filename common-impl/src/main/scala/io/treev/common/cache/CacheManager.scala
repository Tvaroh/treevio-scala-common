package io.treev.common.cache

trait CacheManager[M[_]] {

  def createInMemoryCache(): InMemoryCacheApi[M]

}
