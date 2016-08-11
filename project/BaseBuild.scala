import bintray.BintrayKeys._
import sbt._
import sbt.Keys._
import scoverage.ScoverageSbtPlugin.ScoverageKeys
import wartremover.WartRemover.autoImport._

trait BaseBuild extends Build{

  lazy val buildSettings = Seq(
    organization := "com.agilogy",
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq("2.11.7","2.10.6")
  )

  def commonScalacOptions(scalaVersion: String): Seq[String] = Seq(
    "-deprecation", // Etetmit warning and location for usages of deprecated APIs.
    "-encoding", "UTF-8", // Specify character encoding used by source files. Yes, this is 2 args
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-explaintypes", // Explain type errors in more detail.
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings", // Fail the compilation if there are any warnings."
    "-Xfuture", // Turn on future language features.
    //    "-Xstrict-inference", // Don't infer known-unsound types"
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-numeric-widen" // Warn when numerics are widened.
    //    "-Ywarn-unused-import", // Warn when imports are unused.
    //    "-Ywarn-value-discard" // Warn when non-Unit expression results are unused. Commented out because of WartRemover being strictier
  ) ++ (CrossVersion.partialVersion(scalaVersion) match {
    case Some((2,10)) =>
      Seq(
        "-Xlint"
      )
    case Some((2,11)) =>
      Seq(
        "-Xlint:adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
        "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
        "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
        "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
        "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
        "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
        "-Xlint:doc-detached", // A ScalaDoc comment appears to be detached from its element.
        "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
        "-Xlint:type-parameter-shadow", //A local type parameter shadows a type already in scope.
        "-Xlint:poly-implicit-overload", //Parameterized overloaded implicit methods are not visible as view bounds.
        "-Xlint:option-implicit", //Option.apply used implicit view.
        "-Xlint:delayedinit-select", //Selecting member of DelayedInit.
        "-Xlint:by-name-right-associative", //By-name parameter of right associative operator.
        "-Xlint:package-object-classes", //Class or object defined in package object.
        "-Xlint:unsound-match", //Pattern match may not be typesafe.
        "-Xlint:stars-align", //Pattern sequence wildcard must align with sequence component.
        "-Ywarn-unused" // Warn when local and private vals, vars, defs, and types are unused.
      )
    case _ => Nil
  })

//  lazy val versionSpecificScalacOptions = Seq(
//    scalacOptions ++= {
//      CrossVersion.partialVersion(scalaVersion.value) match {
//        case Some((2, 10)) =>
//          Seq()
//        case Some((2, n)) if n >= 11 =>
//          Seq(
//            "-Ywarn-unused-import",
//            "–Xcheck-null",
//            "–Xcheckinit",
//            "–Xlog-implicits"
//            "-Xdev"
//          )
//      }
//    },
//    scalacOptions in (Compile, console) ~= {_.filterNot("-Ywarn-unused-import" == _)},
//    scalacOptions in (Test, console) <<= (scalacOptions in (Compile, console))
//  )

  lazy val baseSettings = Seq(
    scalacOptions ++= commonScalacOptions(scalaVersion.value),
    libraryDependencies ++= Seq(
    ),
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF"),
    wartremoverErrors ++= Warts.allBut(Wart.DefaultArguments, Wart.Nothing, Wart.NoNeedForMonad, Wart.Overloading)

  ) //++ versionSpecificScalacOptions

  lazy val scoverageSettings = Seq(
    ScoverageKeys.coverageMinimum := 60,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := scalaBinaryVersion.value != "2.10"
    //    ScoverageKeys.coverageExcludedPackages := "cats\\.bench\\..*"
  )

  lazy val publishSettings = Seq(
    bintrayRepository := "scala",
    bintrayOrganization := Some("agilogy"),
    bintrayPackageLabels := Seq("scala"),
    licenses +=("MIT", url("http://opensource.org/licenses/mit-license.php")),
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