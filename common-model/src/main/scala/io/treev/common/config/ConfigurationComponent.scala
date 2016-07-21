package io.treev.common.config

import com.typesafe.config.Config

trait ConfigurationComponent {

  def config: Config

  implicit def toConfigExtensions(config: Config): ConfigExtensions =
    ConfigurationComponent.toConfigExtensions(config)

}

object ConfigurationComponent {

  implicit def toConfigExtensions(config: Config): ConfigExtensions =
    new ConfigExtensions(config)

}
