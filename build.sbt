import sbt._
import io.treev.sbt._
import io.treev.sbt.SbtUtil._

name := "common-parent"
organization in ThisBuild := s"$DefaultOrganization.common"

CommonSettings

lazy val commonModel =
  DefProject("common-model")
    .settings(
      libraryDependencies ++= Seq(
        Deps.CatsCore(Versions.Cats),
        Deps.ScalaTest(Versions.ScalaTest) % Test
      )
    )

lazy val commonTest =
  DefProject("common-test")
    .dependsOn(commonModel)
    .settings(
      libraryDependencies ++= Seq(
        Deps.ScalaTest(Versions.ScalaTest),
        Deps.ScalaCheck(Versions.ScalaCheck),
        Deps.Mockito(Versions.Mockito),
        Deps.JUnit(Versions.JUnit) % Provided
      )
    )
