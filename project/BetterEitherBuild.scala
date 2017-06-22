import sbt._
import sbt.Keys._

object BetterEitherBuild extends BaseBuild {

  lazy val betterEither = project.in(file("."))
    .settings(moduleName := "root")
    .settings(commonSettings)
    .settings(noPublishSettings)
    .aggregate(eitherSyntax,eitherExtras)

  lazy val eitherSyntax = project.in(file("either-syntax"))
    .settings(moduleName := "either-syntax")
    .settings(version := "0.2.rc1")
    .settings(commonSettings)
    //TODO: Make it unavailable for scala 2.12
    .settings(crossScalaVersions := Seq("2.12.2","2.11.8","2.10.6"))
    .settings(
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "3.0.0" % "test"
      )
    )


  lazy val eitherExtras = project.in(file("either-extras"))
    .settings(moduleName := "either-extras")
    .settings(version := "0.2.rc1")
    .settings(commonSettings)
    .settings(crossScalaVersions := Seq("2.12.2","2.11.8","2.10.6"))
    .settings(
      resolvers += Resolver.url("Agilogy Scala",url("http://dl.bintray.com/agilogy/scala/"))(Resolver.ivyStylePatterns),
      libraryDependencies ++= Seq(
        "com.agilogy" %% "classis-monoid" % "0.2",
        "org.scalatest" %% "scalatest" % "3.0.0" % "test"
      )
    )
    // Make it depend on eitherSyntax only for scala <= 2.11
    .dependsOn(eitherSyntax)

}
