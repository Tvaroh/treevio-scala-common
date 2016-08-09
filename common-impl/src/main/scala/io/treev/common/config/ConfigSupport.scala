package io.treev.common.config

import java.util.concurrent.TimeUnit

import com.typesafe.config.Config

import scala.collection.JavaConversions._
import scala.concurrent.duration._

trait ConfigSupport[T] {
  def getValue(config: Config, path: String): T
}

object ConfigSupport {

  def apply[T: ConfigSupport]: ConfigSupport[T] =
    implicitly[ConfigSupport[T]]

  implicit object StringConfigSupport extends ConfigSupport[String] {
    override def getValue(config: Config, path: String): String = config.getString(path)
  }

  implicit object LongConfigSupport extends ConfigSupport[Long] {
    override def getValue(config: Config, path: String): Long = config.getLong(path)
  }

  implicit object IntConfigSupport extends ConfigSupport[Int] {
    override def getValue(config: Config, path: String): Int = config.getInt(path)
  }

  implicit object DoubleConfigSupport extends ConfigSupport[Double] {
    override def getValue(config: Config, path: String): Double = config.getDouble(path)
  }

  implicit object BigDecimalConfigSupport extends ConfigSupport[BigDecimal] {
    override def getValue(config: Config, path: String): BigDecimal = BigDecimal(config.getString(path))
  }

  implicit object BooleanConfigSupport extends ConfigSupport[Boolean] {
    override def getValue(config: Config, path: String): Boolean = config.getBoolean(path)
  }

  implicit object ScalaDurationConfigSupport extends ConfigSupport[FiniteDuration] {
    override def getValue(config: Config, path: String): FiniteDuration =
      config.getDuration(path, TimeUnit.MILLISECONDS).millis
  }

  implicit object JavaDurationConfigSupport extends ConfigSupport[java.time.Duration] {
    override def getValue(config: Config, path: String): java.time.Duration =
      java.time.Duration.ofMillis(config.getDuration(path, TimeUnit.MILLISECONDS))
  }

  implicit object StringListConfigSupport extends ConfigSupport[List[String]] {
    override def getValue(config: Config, path: String): List[String] = config.getStringList(path).toList
  }

  implicit object ConfigConfigSupport extends ConfigSupport[Config] {
    override def getValue(config: Config, path: String): Config = config.getConfig(path)
  }

}
