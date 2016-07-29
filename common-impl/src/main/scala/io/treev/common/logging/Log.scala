package io.treev.common.logging

import org.slf4j.LoggerFactory

import scala.reflect.ClassTag

object Log {

  def apply[T](implicit cls: ClassTag[T]) =
    com.typesafe.scalalogging.Logger(LoggerFactory.getLogger(cls.runtimeClass))

}
