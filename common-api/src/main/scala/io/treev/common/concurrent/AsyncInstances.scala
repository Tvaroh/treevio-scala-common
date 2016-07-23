package io.treev.common.concurrent

import cats._
import cats.data.Xor

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait AsyncInstances extends AsyncInstances1 {

  implicit def asyncInstance(implicit ec: ExecutionContext): MonadError[Async, Throwable] =
    new AsyncCoflatMap with MonadError[Async, Throwable] {

      override def pure[A](a: A): Async[A] =
        Async.pure(a)

      override def pureEval[A](a: Eval[A]): Async[A] =
        Async(() => a match {
          case Now(x) => Future.successful(x)
          case _ => Future(a.value)
        })

      override def map[A, B](fa: Async[A])(f: A => B): Async[B] =
        Async(() => fa.run().map(f))

      override def flatMap[A, B](fa: Async[A])(f: A => Async[B]): Async[B] =
        Async(() => fa.run().flatMap(a => f(a).run()))

      override def handleErrorWith[A](fa: Async[A])
                                     (f: Throwable => Async[A]): Async[A] =
        Async(() => fa.run().recoverWith { case NonFatal(t) => f(t).run() })

      override def raiseError[A](t: Throwable): Async[A] =
        Async.failed(t)

      override def attempt[A](fa: Async[A]): Async[Xor[Throwable, A]] =
        recover[Xor[Throwable, A]](map(fa)(Xor.right)) { case NonFatal(t) => Xor.left(t) }

      override def recover[A](fa: Async[A])
                             (pf: PartialFunction[Throwable, A]): Async[A] =
        Async(() => fa.run().recover(pf))

      override def recoverWith[A](fa: Async[A])
                                 (pf: PartialFunction[Throwable, Async[A]]): Async[A] =
        Async(() => fa.run().recoverWith { case NonFatal(t) => pf(t).run() })

    }

  implicit def asyncGroup[A: Group](implicit ec: ExecutionContext): Group[Async[A]] =
    new AsyncGroup[A]

}

private[concurrent] trait AsyncInstances1 extends AsyncInstances2 {

  implicit def asyncMonoid[A: Monoid](implicit ec: ExecutionContext): Monoid[Async[A]] =
    new AsyncMonoid[A]

}

private[concurrent] trait AsyncInstances2 {

  implicit def asyncSemigroup[A: Semigroup](implicit ec: ExecutionContext): Semigroup[Async[A]] =
    new AsyncSemigroup[A]

}

private abstract class AsyncCoflatMap(implicit ec: ExecutionContext) extends CoflatMap[Async] {

  override def map[A, B](fa: Async[A])(f: A => B): Async[B] = Async(() => fa.run().map(f))

  override def coflatMap[A, B](fa: Async[A])(f: Async[A] => B): Async[B] = Async(() => Future(f(fa)))

}

private class AsyncGroup[A](implicit A: Group[A], ec: ExecutionContext) extends AsyncMonoid[A] with Group[Async[A]] {

  override def inverse(fa: Async[A]): Async[A] = {
    import cats.syntax.group._
    Async(() => fa.run().map(_.inverse))
  }

}

private class AsyncSemigroup[A: Semigroup](implicit ec: ExecutionContext) extends Semigroup[Async[A]] {

  override def combine(fx: Async[A], fy: Async[A]): Async[A] = {
    val xFuture = fx.run()
    val yFuture = fy.run()

    import cats.syntax.semigroup._
    Async(() => xFuture.flatMap(x => yFuture.map(y => x |+| y)))
  }

}

private class AsyncMonoid[A: Monoid](implicit ec: ExecutionContext) extends AsyncSemigroup[A] with Monoid[Async[A]] {

  override def empty: Async[A] =
    Async(() => Future.successful(Monoid[A].empty))

}
