package io.treev.common.exception

abstract class ApiException(message: String) extends RuntimeException(message) {
  def errorCode: String
}
