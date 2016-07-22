package io.treev.common.cassandra.config

case class CassandraApiConfiguration(hosts: Set[String],
                                     user: String,
                                     password: String)
