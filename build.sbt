import sbt._
import io.treev.sbt._
import io.treev.sbt.SbtUtil._
import Settings._

name := "common-parent"
organization in ThisBuild := s"${SbtDefaults.Organization}.common"

CommonSettings

lazy val commonUtil =
  DefProject("common-util")
    .settings(
      libraryDependencies ++= Seq(
        Deps.ScalaTest(Versions.ScalaTest) % Test
      )
    )

lazy val commonTypesafeConfig =
  DefProject("common-typesafe-config")
    .settings(
      libraryDependencies ++= Seq(
        Deps.TypesafeConfig(Versions.TypesafeConfig)
      )
    )

lazy val commonLogging =
  DefProject("common-logging")
    .settings(
      libraryDependencies ++= Seq(
        Deps.Slf4jApi(Versions.Slf4j),
        Deps.ScalaLogging(Versions.ScalaLogging)
      )
    )

lazy val commonTest =
  DefProject("common-test")
    .settings(
      libraryDependencies ++= Seq(
        Deps.ScalaTest(Versions.ScalaTest),
        Deps.ScalaCheck(Versions.ScalaCheck)
      )
    )

lazy val commonTestJUnitSupport =
  DefProject("common-test-junit-support")
    .dependsOn(commonTest)
    .settings(
      libraryDependencies ++= Seq(
        Deps.JUnit(Versions.JUnit)
      )
    )
