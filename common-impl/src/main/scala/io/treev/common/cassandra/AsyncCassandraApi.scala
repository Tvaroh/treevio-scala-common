package io.treev.common.cassandra

import cats.implicits._
import com.datastax.driver.core._
import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}
import io.treev.common.api.Lifecycle
import io.treev.common.cache.CacheManager
import io.treev.common.cassandra.config.CassandraApiConfiguration
import io.treev.common.cassandra.model.ParameterizedQuery
import io.treev.common.concurrent.Async

import scala.concurrent.{ExecutionContext, Future, Promise}

class AsyncCassandraApi(configuration: CassandraApiConfiguration)
                       (cacheManager: CacheManager[Async])
                       (implicit ec: ExecutionContext)
  extends CassandraApi[Async]
  with Lifecycle {

  import AsyncCassandraApi._

  override def start()(implicit ec: ExecutionContext): Async[Unit] =
    Async {
      session
      preparedStatementCache
      ()
    }

  override def stop()(implicit ec: ExecutionContext): Async[Unit] =
    for {
      _ <- session.closeAsync().toAsync
      _ <- cluster.closeAsync().toAsync
    } yield ()

  override def execute[T](query: String, args: AnyRef*)(f: ResultSet => T): Single[T] =
    for {
      preparedStmt <- prepare(query)
      result <- execute(preparedStmt.bind(args: _*)).map(f)
    } yield result

  override def executeBatch(queries: ParameterizedQuery*): Empty =
    for {
      stmts <-
        queries.toList.map { parameterizedQuery =>
          import parameterizedQuery._
          prepare(query).map(_.bind(args: _*))
        }.sequence
      _ <- {
        val batch = new BatchStatement()
        stmts.foreach(batch.add)
        execute(batch)
      }
    } yield ()

  private lazy val cluster: Cluster =
    Cluster.builder()
      .addContactPoints(configuration.hosts.toSeq: _*)
      .withCredentials(configuration.user, configuration.password)
      .build()

  private lazy val session: Session = cluster.connect()

  private implicit lazy val preparedStatementCache = cacheManager.createInMemoryCache()

  private def prepare(query: String): Async[PreparedStatement] =
    preparedStatementCache.caching(query) {
      session.prepareAsync(query).toAsync
    }

  private def execute(stmt: Statement): Async[ResultSet] =
    session.executeAsync(stmt).toAsync

}

object AsyncCassandraApi {

  implicit class ListenableFutureExtensions[T, R](listenableFuture: ListenableFuture[T]) {

    def toScalaFuture: Future[T] = {
      val p = Promise[T]()
      Futures.addCallback(listenableFuture, new FutureCallback[T] {
        def onFailure(t: Throwable): Unit = { p.failure(t); () }
        def onSuccess(result: T): Unit = { p.success(result); () }
      })
      p.future
    }

    def toAsync: Async[T] =
      Async(() => toScalaFuture)

  }

}
