name := "server"

version := "1.0"

lazy val `server` = (project in file(".")).enablePlugins(PlayScala, DockerPlugin)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  filters,
  ws,
  specs2 % Test,
  "com.typesafe.play" %% "play-slick" % "2.0.2",
  "mysql" % "mysql-connector-java" % "5.1.42"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"  