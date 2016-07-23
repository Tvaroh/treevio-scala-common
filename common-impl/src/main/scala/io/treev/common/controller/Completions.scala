package io.treev.common.controller

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.treev.common.exception.ApiException

trait Completions {

  def completeInternalError(apiException: ApiException): Route = {
    val response = FailureResponse(apiException.errorCode)
    val headers =
      response.meta
        .map(_.map({ case (header, value) => RawHeader(header.name, value) }).toList)
        .getOrElse(Nil)
    complete(HttpResponse(StatusCodes.InternalServerError, headers))
  }

}
