name := "Business Manager"

version := "0.0.1"

scalaVersion := "2.11.8"

// For slick
libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "mysql" % "mysql-connector-java" % "5.1.34",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
  "com.zaxxer" % "HikariCP" % "2.4.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.4.177"
)
