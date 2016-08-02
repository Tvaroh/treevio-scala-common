package io.treev.common.json

import io.circe.Decoder._
import io.circe._

trait EncoderDecoder[T] extends Encoder[T] with Decoder[T]

object EncoderDecoder {

  def instance[T](toJson: T => Json, fromJson: HCursor => Result[T]): EncoderDecoder[T] =
    new EncoderDecoder[T] {
      override def apply(t: T): Json = toJson(t)
      override def apply(c: HCursor): Result[T] = fromJson(c)
    }

}
