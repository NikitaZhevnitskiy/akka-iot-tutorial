name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.12.2"

lazy val akkaVersion = "2.5.17"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",

  // https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream
  "com.typesafe.akka" %% "akka-stream" % akkaVersion

)
