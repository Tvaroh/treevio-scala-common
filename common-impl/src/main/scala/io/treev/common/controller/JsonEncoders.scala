package io.treev.common.controller

import io.circe._

trait JsonEncoders {

  implicit val printer: Json => String = Printer.noSpaces.copy(dropNullKeys = true).pretty _

}
