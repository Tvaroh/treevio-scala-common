package io.treev.common.monitoring

import java.util

import io.treev.common.system.ExecutionContextComponent
import org.slf4j.MDC

import scala.concurrent.ExecutionContext

/** See [[https://yanns.github.io/blog/2014/05/04/slf4j-mapped-diagnostic-context-mdc-with-play-framework/]]. */
class MDCPropagatingExecutionContext(delegate: ExecutionContext) extends ExecutionContext {
  self =>

  override def prepare(): ExecutionContext = new ExecutionContext {
    val mdcContext = MDC.getCopyOfContextMap

    override def execute(runnable: Runnable): Unit = self.execute(
      new Runnable {
        override def run(): Unit = {
          // backup the callee MDC context
          val oldMDCContext = MDC.getCopyOfContextMap

          // run the runnable with the captured context
          setMdcContext(mdcContext)

          try {
            runnable.run()
          } finally {
            // restore the callee MDC context
            setMdcContext(oldMDCContext)
          }
        }
      }
    )

    def reportFailure(t: Throwable): Unit = self.reportFailure(t)

  }

  override def execute(r: Runnable): Unit = delegate.execute(r)

  override def reportFailure(t: Throwable): Unit = delegate.reportFailure(t)

  private def setMdcContext(context: util.Map[String, String]): Unit = {
    Option(context).fold(MDC.clear())(MDC.setContextMap)
  }

}

object MDCPropagatingExecutionContext {

  def apply(delegate: ExecutionContext): ExecutionContext =
    new MDCPropagatingExecutionContext(delegate)

}

trait MDCPropagatingExecutionContextComponent extends ExecutionContextComponent {

  override implicit val executionContext: ExecutionContext =
    MDCPropagatingExecutionContext(delegate)

  protected def delegate: ExecutionContext

}
