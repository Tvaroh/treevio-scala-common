package io.treev.common.config

import com.typesafe.config.Config

class ConfigExtensions(val config: Config) extends AnyVal {

  def get[T: ConfigSupport](path: String): Option[T] =
    if (config.hasPath(path)) Some(getValueByType(config, path))
    else None

  def getOrElse[T: ConfigSupport](path: String, default: => T): T =
    get(path).getOrElse(default)

  private def getValueByType[T: ConfigSupport](config: Config, path: String): T =
    ConfigSupport[T].getValue(config, path)

}

object ConfigExtensions {

  implicit def toConfigExtensions(config: Config): ConfigExtensions =
    new ConfigExtensions(config)

}
