name := "BlackBox"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "org.plotly-scala" %% "plotly-render" % "0.4.0"

libraryDependencies  ++= Seq(
  // Last stable release
  "org.scalanlp" %% "breeze" % "0.13.2",

  // Native libraries are not included by default. add this if you want them (as of 0.7)
  // Native libraries greatly improve performance, but increase jar sizes.
  // It also packages various blas implementations, which have licenses that may or may not
  // be compatible with the Apache License. No GPL code, as best I know.
  "org.scalanlp" %% "breeze-natives" % "0.13.2",

  // The visualization library is distributed separately as well.
  // It depends on LGPL code
  "org.scalanlp" %% "breeze-viz" % "0.13.2"
)
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.6.4"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.4.0"
libraryDependencies += "org.scalamock" %% "scalamock" % "4.1.0" % "test"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.14"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.14" % Test

libraryDependencies += "com.wix" %% "accord-core" % "0.7.2"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"