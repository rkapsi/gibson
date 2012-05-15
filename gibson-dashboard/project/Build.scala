import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName = "gibson-dashboard"
    val appVersion = "0.1-SNAPSHOT"
    
    val appDependencies = Seq(
      "org.ardverk.gibson" % "gibson-core" % "0.1-SNAPSHOT",
      "com.google.inject" % "guice" % "3.0",
      "ch.qos.logback" % "logback-core" % "1.0.3"
    )
    
    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
    )
}