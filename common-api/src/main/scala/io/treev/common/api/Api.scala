package io.treev.common.api

import akka.NotUsed
import akka.stream.scaladsl.Source

trait Api[M[_]] {

  type Empty = M[Unit]
  type Single[Out] = M[Out]
  type Optional[Out] = M[Option[Out]]
  type Many[Out] = M[Source[Out, NotUsed]]

}
