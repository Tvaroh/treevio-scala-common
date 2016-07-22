package io.treev.common.cassandra.model

case class ParameterizedQuery(query: String, args: AnyRef*)
