package io.treev.common.cassandra.config

case class CassandraApiConfiguration(hosts: Set[String],
                                     username: String,
                                     password: String)
