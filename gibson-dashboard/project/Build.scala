import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName = "gibson-dashboard"
    val appVersion = "0.2.6"
    
    val appDependencies = Seq(
      "org.ardverk.gibson" % "gibson-core" % appVersion,
      "com.google.inject" % "guice" % "3.0",
      "ch.qos.logback" % "logback-core" % "1.0.7"
    )
    
    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
      resolvers += "Ardverk Maven Repository" at "http://mvn.ardverk.org/repository/release"
    )
}
