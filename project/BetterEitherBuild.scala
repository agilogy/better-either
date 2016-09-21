import sbt._
import sbt.Keys._

object BetterEitherBuild extends BaseBuild {

  lazy val betterEither = project.in(file("."))
    .settings(moduleName := "root")
    .settings(version := "0.1.1-20160810")
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
    .settings(version := "0.1.1-20160810")
    .settings(commonSettings)
    .settings(
      resolvers += Resolver.url("Agilogy Scala",url("http://dl.bintray.com/agilogy/scala/"))(Resolver.ivyStylePatterns),
      libraryDependencies ++= Seq(
        "com.agilogy" %% "classis-monoid" % "0.2.rc1",
        "org.scalatest" %% "scalatest" % "2.2.4" % "test"
      )
    )
    .dependsOn(eitherSyntax)

}
