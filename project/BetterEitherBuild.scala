import sbt._
import sbt.Keys._

object BetterEitherBuild extends BaseBuild {

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.0" % "test"

  lazy val betterEither = project.in(file("."))
    .settings(moduleName := "root")
    .settings(commonSettings)
    .settings(noPublishSettings)
    .aggregate(eitherSyntax,eitherExtras)

  lazy val commonSourcesSettings = {
    unmanagedSourceDirectories in Compile ++= {
      CrossVersion.partialVersion(scalaVersion.value)  match {
        case Some((2, mi)) if mi<12 => Seq(sourceDirectory.value / "main" / "scala-pre-2.12")
        case _ => Seq()
      }
    }
  }


  lazy val eitherSyntax = project.in(file("either-syntax"))
    .settings(moduleName := "either-syntax")
    .settings(version := "0.2.rc1")
    .settings(commonSettings)
    .settings(commonSourcesSettings)
    .settings(
      libraryDependencies ++= Seq(scalaTest)
    )


  lazy val eitherExtras = project.in(file("either-extras"))
    .settings(moduleName := "either-extras")
    .settings(version := "0.2.rc1")
    .settings(commonSettings)
    .settings(commonSourcesSettings)
    .settings(
      resolvers += Resolver.url("Agilogy Scala",url("http://dl.bintray.com/agilogy/scala/"))(Resolver.ivyStylePatterns),
      libraryDependencies ++= Seq(
        "com.agilogy" %% "classis-monoid" % "0.2",
        scalaTest
      )
    )
    .dependsOn(eitherSyntax)

}
