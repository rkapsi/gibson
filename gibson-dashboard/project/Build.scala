import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName = "gibson-dashboard"
    val appVersion = "0.2.8"
    
    val appDependencies = Seq(
      "org.ardverk.gibson" % "gibson-core" % appVersion,
      "com.google.inject" % "guice" % "3.0",
      "ch.qos.logback" % "logback-core" % "1.0.7",
      javaCore
    )
    
    val main = play.Project(appName, appVersion, appDependencies).settings(
      resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
      resolvers += "Ardverk Maven Repository" at "http://mvn.ardverk.org/repository/release"
    )
}
