package io.treev.common.test

import org.scalacheck.{Arbitrary, Gen}

trait Generators {

  implicit val arbUUID = Arbitrary(Gen.uuid)

}
