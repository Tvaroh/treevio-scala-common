package io.treev.common.cassandra.config

trait CassandraApiConfiguration {
  def hosts: Set[String]
  def user: String
  def password: String
}
