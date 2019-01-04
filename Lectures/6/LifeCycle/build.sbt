lazy val root = (project in file(".")).
settings (
  name := "Life Cycle Examples",
  version := "1.0",
  scalaVersion := "2.12.1",
  scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation"),
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.17"
)
