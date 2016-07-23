package io.treev.common.cassandra

import com.datastax.driver.core.ResultSet
import io.treev.common.api.Api
import io.treev.common.cassandra.model.ParameterizedQuery

trait CassandraApi[M[_]] extends Api[M] {

  def execute[T](query: String, args: AnyRef*)(f: ResultSet => T): Single[T]
  def executeIgnoreResult(query: String, args: AnyRef*): Empty
  def executeBatch(queries: ParameterizedQuery*): Empty

}
