package io.treev.common.system

import scala.concurrent.ExecutionContext

trait ExecutionContextComponent {
  implicit def executionContext: ExecutionContext
}
