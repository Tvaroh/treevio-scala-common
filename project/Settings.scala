import io.treev.sbt._
import sbt.Keys._

object Settings {

  implicit val projectSettings: ProjectSettings = ProjectSettings(
    scalaVersion := Versions.Scala
  )

}
