package io.treev.common.concurrent

import cats.kernel.Monoid
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AsyncSpec extends FlatSpec with Matchers with ScalaFutures {

  "Async" should "support map operation" in {
    import cats.syntax.functor._

    val initialValue = 1
    val f: Int => String = _.toString
    whenReady(Async(() => Future.successful(initialValue)).map(f).run()) {
      _ should be (f(initialValue))
    }
  }

  it should "support pure operation" in {
    import cats.syntax.applicative._

    val value = 1
    whenReady(value.pure[Async].run()) {
      _ should be (value)
    }
  }

  it should "support cartesian syntax" in {
    import cats.syntax.applicative._
    import cats.syntax.cartesian._

    val (value1, value2) = (1, 2)
    def f(i1: Int, i2: Int): Int = i1 + i2

    whenReady((value1.pure[Async] |@| value2.pure[Async]).map(f).run()) {
      _ should be (f(value1, value2))
    }
  }

  it should "support flatMap operation" in {
    import cats.syntax.applicative._
    import cats.syntax.flatMap._

    val initialValue = 1
    val f: Int => String = _.toString
    whenReady(initialValue.pure[Async].flatMap(f(_).pure[Async]).run()) {
      _ should be (f(1))
    }
  }

  it should "support combine operation" in {
    import cats.implicits._

    val (value1: Int, value2: Int) = (1, 2)
    whenReady((value1.pure[Async] |+| value2.pure[Async]).run()) {
      _ should be (value1 |+| value2)
    }
  }

  it should "support empty operation" in {
    import cats.implicits._

    whenReady(Monoid[Async[Int]].empty.run()) {
      _ should be (Monoid[Int].empty)
    }
  }

}
