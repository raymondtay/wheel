name := "wheel"

version := "1.0"

description := "Build JSON models from a schema"

scalaVersion := "2.12.4"

val circeVersion = "0.9.1"

scalacOptions ++= Seq("-deprecation")

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-optics"
).map(_ % circeVersion)
libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1"
libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "4.0.2" % "test")

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)

resolvers += Resolver.sonatypeRepo("releases")

/* experiment with shapeless */
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)
libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.3"
)
/* Experiment with testGrouping */

testForkedParallel in Test := false

concurrentRestrictions in Global := Tags.limit(Tags.ForkedTestGroup, 2) :: Nil
import Tests._


  def groupByFirst(tests: Seq[TestDefinition]) =
    tests groupBy (_.name(0)) map {
      case (letter, tests) =>
        val options = ForkOptions().withRunJVMOptions(Vector("-D-J-Xmx2048m -Dfirst.letter"+letter))
        println(s"Tests....${tests.foreach(t => println("++>"+t.name))}, $options")
        new Group(letter.toString, tests, SubProcess(options))
    } toSeq

    testGrouping in Test := groupByFirst( (definedTests in Test).value )

    val ray = taskKey[Unit]("testing key")
    ray in Compile := { println("Heeeellll yahhhhh") }
    ray in Test := { println("Heeeellll yahhhhh !!!") }

