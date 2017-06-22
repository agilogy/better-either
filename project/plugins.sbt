resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "1.2.0")

//addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")

//addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.5.0")

resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
  Resolver.ivyStylePatterns)

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")