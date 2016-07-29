import sbt._
import io.treev.sbt._
import io.treev.sbt.SbtUtil._

name := "common-parent"
organization in ThisBuild := s"$DefaultOrganization.common"

CommonSettings()

lazy val commonApi =
  DefProject("common-api")
    .settings(
      libraryDependencies ++= Seq(
        Deps.CatsCore(Versions.Cats),
        Deps.Monix(Versions.Monix), Deps.MonixCats(Versions.Monix),
        Deps.ScalaTest(Versions.ScalaTest) % Test
      )
    )

lazy val commonImpl =
  DefProject("common-impl")
    .dependsOn(commonApi)
    .settings(
      libraryDependencies ++= Seq(
        Deps.TypesafeConfig(Versions.TypesafeConfig),
        Deps.Slf4jApi(Versions.Slf4j), Deps.Logback(Versions.Logback), Deps.ScalaLogging(Versions.ScalaLogging),
        Deps.CirceCore(Versions.Circe) % Provided,
        Deps.AkkaHttp(Versions.Akka) % Provided,
        Deps.CassandraDriver(Versions.CassandraDriver) % Provided,
        Deps.ScalaCacheCore(Versions.ScalaCache) % Provided,
        Deps.ScalaTest(Versions.ScalaTest) % Test
      )
    )

lazy val commonTest =
  DefProject("common-test")
    .dependsOn(commonImpl)
    .settings(
      libraryDependencies ++= Seq(
        Deps.ScalaTest(Versions.ScalaTest),
        Deps.ScalaCheck(Versions.ScalaCheck),
        Deps.Mockito(Versions.Mockito),
        Deps.JUnit(Versions.JUnit) % Provided
      )
    )
