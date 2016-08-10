import sbt._
import sbt.Keys._

object BetterEitherBuild extends BaseBuild {

  lazy val betterEither = project.in(file("."))
    .settings(moduleName := "root")
    .settings(version := "0.1")
    .settings(commonSettings)
    .settings(noPublishSettings)
    .aggregate(eitherSyntax,eitherExtras)

  lazy val eitherSyntax = project.in(file("either-syntax"))
    .settings(moduleName := "either-syntax")
    .settings(version := "0.1")
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "2.2.4" % "test"
      )
    )

  lazy val eitherExtras = project.in(file("either-extras"))
    .settings(moduleName := "either-extras")
    .settings(version := "0.1")
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "2.2.4" % "test"
      )
    )
    .dependsOn(eitherSyntax)

}
