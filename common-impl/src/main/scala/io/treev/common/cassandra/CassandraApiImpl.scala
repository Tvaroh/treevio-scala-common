package io.treev.common.cassandra

import com.datastax.driver.core._
import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}
import io.treev.common.api.Lifecycle
import io.treev.common.cache.CacheManager
import io.treev.common.cassandra.config.CassandraApiConfiguration
import io.treev.common.cassandra.model.ParameterizedQuery
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.{Future, Promise}

class CassandraApiImpl(configuration: CassandraApiConfiguration)
                      (cacheManager: CacheManager)
                      (implicit scheduler: Scheduler)
  extends CassandraApi
  with Lifecycle {

  import CassandraApiImpl._

  override def start()(implicit scheduler: Scheduler): Task[Unit] =
    Task {
      session
      preparedStatementCache
      ()
    }

  override def stop()(implicit scheduler: Scheduler): Task[Unit] =
    for {
      _ <- Task.defer(Task.fromFuture(session.closeAsync().toScalaFuture))
      _ <- Task.fromFuture(cluster.closeAsync().toScalaFuture)
    } yield ()

  override def execute[T](query: String, args: AnyRef*)(f: ResultSet => T): Task[T] =
    for {
      preparedStmt <- prepare(query)
      result <- execute(preparedStmt.bind(args: _*)).map(f)
    } yield result

  override def executeIgnoreResult(query: String, args: AnyRef*): Task[Unit] =
    execute[Unit](query, args: _*)(_ => ())

  override def executeBatch(queries: ParameterizedQuery*): Task[Unit] =
    for {
      stmts <-
        Task.gather {
          queries.toList.map { parameterizedQuery =>
            import parameterizedQuery._
            prepare(query).map(_.bind(args: _*))
          }
        }
      _ <- {
        val batch = new BatchStatement()
        stmts.foreach(batch.add)
        execute(batch)
      }
    } yield ()

  private lazy val cluster: Cluster =
    Cluster.builder()
      .addContactPoints(configuration.hosts.toSeq: _*)
      .withCredentials(configuration.username, configuration.password)
      .build()

  private lazy val session: Session = cluster.connect()

  private implicit lazy val preparedStatementCache = cacheManager.createInMemoryCache()

  private def prepare(query: String): Task[PreparedStatement] =
    preparedStatementCache.caching(query) {
      Task.defer(Task.fromFuture(session.prepareAsync(query).toScalaFuture))
    }

  private def execute(stmt: Statement): Task[ResultSet] =
    Task.defer(Task.fromFuture(session.executeAsync(stmt).toScalaFuture))

}

object CassandraApiImpl {

  implicit class ListenableFutureExtensions[T, R](listenableFuture: ListenableFuture[T]) {

    def toScalaFuture: Future[T] = {
      val p = Promise[T]()
      Futures.addCallback(listenableFuture, new FutureCallback[T] {
        override def onFailure(t: Throwable): Unit = { p.failure(t); () }
        override def onSuccess(result: T): Unit = { p.success(result); () }
      })
      p.future
    }

  }

}
