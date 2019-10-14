ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.13.1"

lazy val root = (project in file("."))
  .settings(
    name := "sgit",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "com.github.scopt" %% "scopt" % "4.0.0-RC2",
      "org.scalactic" % "scalactic_2.13" % "3.0.8"
    )
  )

import sbtassembly.AssemblyPlugin.defaultUniversalScript

assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultUniversalScript(shebang = false)))
assemblyJarName in assembly := s"${name.value}.bat"
parallelExecution in Test := false
