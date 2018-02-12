name := "wheel"

version := "1.0"

description := "Build JSON models from a schema"

scalaVersion := "2.12.4"

val circeVersion = "0.9.1"

scalacOptions ++= Seq("-deprecation")

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-optics"
).map(_ % circeVersion)

libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1"

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)

resolvers += Resolver.sonatypeRepo("releases")


