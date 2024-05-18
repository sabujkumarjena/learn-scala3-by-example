

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.1"

lazy val root = (project in file("."))
  .settings(
    name := "learn-scala3-by-example",
    scalacOptions ++= Seq (
      "-Xcheck-macros"
    ),
    libraryDependencies += "org.scala-lang" %% "scala3-tasty-inspector" % scalaVersion.value
  )
