import sbt._

object Deps {

  def TypesafeConfig(version: String): ModuleID = "com.typesafe" % "config" % version

  def CatsCore(version: String): ModuleID = "org.typelevel" %% "cats-core" % version

  def CirceCore(version: String): ModuleID = "io.circe" %% "circe-core" % version

  def MonixEval(version: String): ModuleID = "io.monix" %% "monix-eval" % version

  def AkkaHttp(version: String): ModuleID = "com.typesafe.akka" %% "akka-http" % version

  def Slf4jApi(version: String): ModuleID = "org.slf4j" % "slf4j-api" % version
  def ScalaLogging(version: String): ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % version

  def ScalaTest(version: String): ModuleID = "org.scalatest" %% "scalatest" % version
  def ScalaCheck(version: String): ModuleID = "org.scalacheck" %% "scalacheck" % version
  def JUnit(version: String): ModuleID = "junit" % "junit" % version

}
