package io.treev.common.data

trait Tiny[T] extends Any {
  self: AnyVal =>

  def value: T

  final def unwrap: T = value
}

trait TinyString[A <: TinyString[A]] extends Any with Tiny[String] {
  self: AnyVal with A =>

  def isEmpty = value.isEmpty

}
