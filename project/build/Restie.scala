import java.io.File
import sbt._

class Restie(info: ProjectInfo) extends DefaultProject(info) with maven.MavenDependencies {



  override def compileOptions = super.compileOptions ++
    Seq("-deprecation",
        "-Xmigration",
        "-Xcheckinit",
        "-Xwarninit",
        "-encoding", "utf8")
        .map(CompileOption(_))

//  val scalatoolsSnapshot = "Scala Tools Snapshot" at "http://scala-tools.org/repo-snapshots/"
  val scalatoolsRelease = "Scala Tools Snapshot" at "http://scala-tools.org/repo-releases/"

//  val scalaTest = "org.scalatest" % "scalatest" % "1.2"
  val junitInterface = "com.novocode" % "junit-interface" % "0.5"
}
