package io.treev.common.json

import java.util.UUID

import io.circe._

trait JsonEncoders {

  implicit val printer: Json => String = Printer.noSpaces.copy(dropNullKeys = true).pretty

  implicit val uuidEncoder: EncoderDecoder[UUID] =
    EncoderDecoder.instance(
      uuid => Json.fromString(uuid.toString),
      _.as[String].map(s => UUID.fromString(s))
    )

}

object JsonEncoders extends JsonEncoders
