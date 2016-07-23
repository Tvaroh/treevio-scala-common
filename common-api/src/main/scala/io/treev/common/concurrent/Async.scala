package io.treev.common.concurrent

import scala.concurrent.{ExecutionContext, Future}

trait Async[+A] {
  def run(): Future[A]
}

object Async extends AsyncInstances {

  def apply[A](run: () => Future[A]): Async[A] = new AsyncImpl(run)
  def apply[A](comp: => A)(implicit ec: ExecutionContext): Async[A] = new AsyncImpl(() => Future(comp))

  def pure[A](a: A): Async[A] = new AsyncImpl(() => Future.successful(a))
  def failed[A](t: Throwable): Async[A] = new AsyncImpl(() => Future.failed(t))

}

private class AsyncImpl[A](_run: () => Future[A]) extends Async[A] {

  override def run(): Future[A] = _run()

}
