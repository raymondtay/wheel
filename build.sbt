<<<<<<< HEAD

=======
>>>>>>> aa75bcf87378e19e6f43b6f21e838cec918e3285
name := "wheel"

version := "1.0"

<<<<<<< HEAD
description := "Build JSON models from a schema"

scalaVersion := "2.12.4"

val circeVersion = "0.9.1"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
=======
scalaVersion := "2.12.4"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
val circeVersion = "0.9.1"
>>>>>>> aa75bcf87378e19e6f43b6f21e838cec918e3285

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
<<<<<<< HEAD
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-optics"
).map(_ % circeVersion)

libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1"

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
=======
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
>>>>>>> aa75bcf87378e19e6f43b6f21e838cec918e3285


