name := "wheel"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
val circeVersion = "0.9.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)


