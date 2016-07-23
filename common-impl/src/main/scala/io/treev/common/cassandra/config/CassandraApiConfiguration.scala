package io.treev.common.cassandra.config

trait CassandraApiConfiguration {
  def hosts: Set[String]
  def username: String
  def password: String
}

object CassandraApiConfiguration {
  def apply(_hosts: Set[String],
            _username: String,
            _password: String): CassandraApiConfiguration =
    new CassandraApiConfiguration {
      override def hosts: Set[String] = _hosts
      override def username: String = _username
      override def password: String = _password
    }
}
