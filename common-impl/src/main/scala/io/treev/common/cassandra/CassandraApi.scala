package io.treev.common.cassandra

import com.datastax.driver.core.ResultSet
import io.treev.common.cassandra.model.ParameterizedQuery
import monix.eval.Task

trait CassandraApi {

  def execute[T](query: String, args: AnyRef*)(f: ResultSet => T): Task[T]
  def executeIgnoreResult(query: String, args: AnyRef*): Task[Unit]
  def executeBatch(queries: ParameterizedQuery*): Task[Unit]

}
