package io.treev.common.controller

sealed trait ResponseHeader {
  def name: String
}
object ResponseHeader {
  case object ErrorCode extends ResponseHeader { override def name: String = "Error-Code" }
  case object CorrelationId extends ResponseHeader { override def name: String = "Correlation-Id" }
}

sealed abstract class ResponseBase(val meta: Option[Map[ResponseHeader, String]])

class FailureResponse private(meta: Option[Map[ResponseHeader, String]] = None) extends ResponseBase(meta)
object FailureResponse {
  def apply(errorCode: String): FailureResponse =
    new FailureResponse(Some(Map(ResponseHeader.ErrorCode -> errorCode)))
}
