package io.treev.common.concurrent

import cats._

import scala.concurrent.{ExecutionContext, Future}

trait AsyncInstances extends AsyncInstances1 {

  implicit def asyncFunctor(implicit ec: ExecutionContext): Functor[Async] =
    new AsyncFunctor

  implicit def asyncApplicative(implicit ec: ExecutionContext): Applicative[Async] =
    new AsyncApplicative

  implicit def asyncMonad(implicit ec: ExecutionContext): Monad[Async] =
    new AsyncMonad

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

private class AsyncFunctor(implicit ec: ExecutionContext) extends Functor[Async] {
  override def map[A, B](fa: Async[A])(f: A => B): Async[B] =
    Async(() => fa.run().map(f))
}

private class AsyncApplicative(implicit ec: ExecutionContext) extends Applicative[Async] {
  override def pure[A](a: A): Async[A] =
    Async(() => Future.successful(a))
  override def ap[A, B](ff: Async[A => B])(fa: Async[A]): Async[B] =
    Async(() => fa.run().flatMap(a => ff.run().map(_(a))))
}

private class AsyncMonad(implicit ec: ExecutionContext) extends AsyncApplicative with Monad[Async] {
  override def flatMap[A, B](fa: Async[A])(f: A => Async[B]): Async[B] =
    Async(() => fa.run().flatMap(a => f(a).run()))
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
