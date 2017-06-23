import bintray.BintrayKeys._
import sbt._
import sbt.Keys._
import scoverage.ScoverageKeys._

trait BaseBuild extends Build{

  lazy val testSettings = Seq(
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )

  lazy val buildSettings = Seq(
    organization := "com.agilogy",
    scalaVersion := "2.12.2",
    crossScalaVersions := Seq("2.12.2","2.11.8","2.10.6"),
    libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.10.0",
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
        case Some((2, scalaMajor)) if scalaMajor >= 11 => Seq()
        // in Scala 2.10, quasiquotes are provided by macro paradise
        case Some((2, 10)) =>
          Seq(
            "org.scalamacros" %% "quasiquotes" % "2.1.0" cross CrossVersion.binary
          )
      }
    }
  )

  lazy val commonScalacOptions = Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    //    "-language:existentials",
    //    "-language:higherKinds",
    //    "-language:implicitConversions",
    //    "-language:experimental.macros",
    "-unchecked",
    "-Yno-adapted-args",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture",
    "-Xfatal-warnings",
    "-Ywarn-dead-code"
    //    "-P:linter:disable:PreferIfToBooleanMatch"
  )

  lazy val otherScalacOptions = Seq(
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) =>
          Seq("-Xlint")
        case Some((2, 11)) =>
          Seq(
            "-Xlint:_",
            "-Ywarn-unused-import",
            "-Yinline-warnings" //??
          )
        case Some((2,12)) =>
          Seq(
            // Avoid unused import warnings because either.syntax import is indeed unused in 2.12
//            "-Xlint",
//            "-Ywarn-unused"
            "-Xlint:-unused,_",
            "-Ywarn-unused:-imports,_"
          )
        case _ =>
          Seq()
      }
    },
    scalacOptions in (Compile, console) ~= {_.filterNot("-Ywarn-unused-import" == _)},
    scalacOptions in (Test, console) <<= (scalacOptions in (Compile, console))
  )

  lazy val baseSettings = Seq(
    scalacOptions ++= commonScalacOptions,
    libraryDependencies ++= Seq(
    ),
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF")
  ) ++ otherScalacOptions

  lazy val scoverageSettings = Seq(
    coverageMinimum := 60,
    coverageFailOnMinimum := false,
    coverageHighlighting := scalaBinaryVersion.value != "2.10"
    //    ScoverageKeys.coverageExcludedPackages := "cats\\.bench\\..*"
  )

  lazy val publishSettings = Seq(
    bintrayRepository := "scala",
    bintrayOrganization := Some("agilogy"),
    bintrayPackageLabels := Seq("scala"),
    licenses +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    publishMavenStyle := isSnapshot.value,
    publishTo := {
      val nexus = "http://188.166.95.201:8081/content/repositories/snapshots"
      if (isSnapshot.value) Some("snapshots"  at nexus)
      else publishTo.value
    },
    bintrayReleaseOnPublish := !isSnapshot.value
  )

  lazy val noPublishSettings = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )

  lazy val commonSettings = buildSettings ++ baseSettings ++ scoverageSettings ++ publishSettings

}