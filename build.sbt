ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version      := "0.1-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Sgit",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "com.github.scopt" %% "scopt" % "4.0.0-RC2"
    ),
    mainClass in Compile := Some("Sgit")
  )

import sbtassembly.AssemblyPlugin.defaultShellScript

assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript))

assemblyJarName in assembly := s"${name.value}-${version.value}"