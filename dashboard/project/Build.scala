import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "dashboard"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.google.inject" % "guice" % "3.0",
      "com.basho.riak" % "riak-client" % "1.0.5"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
