package io.treev.common.monitoring

import org.slf4j.MDC

trait MDCUtil {

  def clear(): Unit = {
    MDC.clear()
  }

  def put(key: String, value: String): Unit = {
    MDC.put(key, value)
  }

  def get(key: String): Option[String] =
    Option(MDC.get(key))

}

object MDCUtil extends MDCUtil
