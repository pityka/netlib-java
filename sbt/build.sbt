
inThisBuild(
  List(
    organization := "io.github.pityka",
    homepage := Some(url("https://pityka.github.io/netlib-java/")),
    licenses := List(("BSD 3 Clause", url("http://opensource.org/licenses/BSD-3-Clause"))),
    developers := List(
    Developer(
        "pityka",
        "Istvan Bartha",
        "bartha.pityu@gmail.com",
        url("https://github.com/pityka/netlib-java")
      ),
    ),
    parallelExecution := false,
    scmInfo := Some(
      ScmInfo(
        url("https://pityka.github.io/netlib-java/"),
        "scm:git@github.com:pityka/netlib-java.git"
      )
    )
  )
)

val commonSettings = Seq(
  autoScalaLibrary := false,
  crossPaths := false,
  organization := "io.github.pityka",
  licenses += ("BSD 3 Clause", url("http://opensource.org/licenses/BSD-3-Clause")),
  publishTo := sonatypePublishTo.value,
  javacOptions ++= Seq("-source","1.8","-target","1.8")
)

lazy val jniOsx = project
  .in(file("jni-osx"))
  .settings(commonSettings)
  .settings(
    name := "netlib-java-jni-osx",
  )

  lazy val jniLinux = project
  .in(file("jni-linux"))
  .settings(commonSettings)
  .settings(
    name := "netlib-java-jni-linux",
  )

lazy val core = project
  .in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "netlib-java",
    libraryDependencies ++= Seq(
      "com.github.fommil" % "jniloader" % "1.1",
      "net.sourceforge.f2j" % "arpack_combined_all" % "0.1",
    ),
    Compile/doc/sources := List.empty
  )
  .dependsOn(jniOsx)
  .dependsOn(jniLinux)

lazy val test = project.in(file("test"))
  .settings(commonSettings)
  .settings(
    publishArtifact := false,
    publish / skip := true
  )
   .settings(
    libraryDependencies ++= Seq(
      "com.github.sbt" % "junit-interface" % "0.13.3" 
    ),
    Compile/mainClass := Some("BLASTest"),
    Compile/doc/sources := List.empty
  ).dependsOn(core)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    publishArtifact := false,
    publish / skip := true
  )
  .aggregate(jniOsx, jniLinux, core)

publishArtifact := false

publish / skip := true
