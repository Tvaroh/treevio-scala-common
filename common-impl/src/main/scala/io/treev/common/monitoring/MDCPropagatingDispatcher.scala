package io.treev.common.monitoring

import akka.dispatch._
import com.typesafe.config.Config
import io.treev.common.config.ConfigExtensions._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class MDCPropagatingDispatcherConfigurator(config: Config, prerequisites: DispatcherPrerequisites)
  extends MessageDispatcherConfigurator(config, prerequisites) {

  override def dispatcher(): MessageDispatcher = instance

  private val instance = new MDCPropagatingDispatcher(
    this,
    config.getString("id"),
    config.getInt("throughput"),
    config.getOrElse[FiniteDuration]("throughput-deadline-time", 0.seconds),
    configureExecutor(),
    config.getOrElse[FiniteDuration]("shutdown-timeout", 5.seconds)
  )

}

class MDCPropagatingDispatcher(_configurator: MessageDispatcherConfigurator,
                               id: String,
                               throughput: Int,
                               throughputDeadlineTime: Duration,
                               executorServiceFactoryProvider: ExecutorServiceFactoryProvider,
                               shutdownTimeout: FiniteDuration)
  extends Dispatcher(
    _configurator,
    id,
    throughput,
    throughputDeadlineTime,
    executorServiceFactoryProvider,
    shutdownTimeout
  ) {

  override def prepare(): ExecutionContext = delegate.prepare()

  private val delegate = MDCPropagatingExecutionContext(this)

}

