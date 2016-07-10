package io.treev.common.test

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.Checkers
import org.scalatest.{Matchers, Suite}

trait SpecBase
  extends Suite
    with Matchers
    with Checkers
    with ScalaFutures
    with MockitoSugar
    with PatienceConfig {

  protected implicit val effectivePatience: PatienceConfig = defaultPatience

  override implicit val generatorDrivenConfig = PropertyCheckConfig(minSuccessful = 100)

}
