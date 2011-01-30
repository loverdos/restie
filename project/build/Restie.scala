import java.io.File
import java.net.URL
import java.util.Calendar
import sbt._

class Restie(info: ProjectInfo) extends DefaultProject(info) {
  override def compileOptions = super.compileOptions ++
    Seq("-deprecation",
        "-Xmigration",
        "-Xcheckinit",
        "-Xwarninit",
        "-encoding", "utf8")
        .map(CompileOption(_))

  def extraResources = "LICENSE.txt"
  override def mainResources = super.mainResources +++ extraResources

  val lib_slf4j           = "org.slf4j"      % "slf4j-api"       % "1.6.1"  % "compile"
  val lib_servlet         = "javax.servlet"  % "servlet-api"     % "2.5"    % "provided"
  val lib_logback_simple  = "ch.qos.logback" % "logback-classic" % "0.9.28" % "test"
  val lib_junit_interface = "com.novocode"   % "junit-interface" % "0.5"    % "test"

  override def packageDocsJar = defaultJarPath("-javadoc.jar")
  override def packageSrcJar= defaultJarPath("-sources.jar")
  val sourceArtifact = Artifact.sources(artifactID)
  val docsArtifact = Artifact.javadoc(artifactID)
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageDocs, packageSrc)
  
  override def managedStyle = ManagedStyle.Maven

  override def pomExtra =
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <developers>
      <developer>
        <id>loverdos</id>
        <name>Christos KK Loverdos</name>
      </developer>
    </developers>;

  override def repositories =
    if (version.toString.endsWith("-SNAPSHOT")) super.repositories + ScalaToolsSnapshots
    else super.repositories
  
  // Set up publish repository (the tuple avoids SBT's ReflectiveRepositories detection)
  private lazy val ScalaToolsReleases_t  = ("Scala Tools Releases"  -> "http://nexus.scala-tools.org/content/repositories/releases/")
  private lazy val ScalaToolsSnapshots_t = ("Scala Tools Snapshots" -> "http://nexus.scala-tools.org/content/repositories/snapshots/")

  val publishTo =
    if (version.toString.endsWith("-SNAPSHOT")) {
      println("====> publishing SNAPSHOT: " + version)
      ScalaToolsSnapshots_t._1 at ScalaToolsSnapshots_t._2
    }
    else {
      println("====> publishing RELEASE: " + version)
      ScalaToolsReleases_t._1 at ScalaToolsReleases_t._2
    }

  Credentials(Path.userHome / ".ivy2" / ".credentials", log)

  lazy val publishRemote = propertyOptional[Boolean](false, true)
  
  private lazy val localDestRepo = Resolver.file("maven-local", Path.userHome / ".m2" / "repository" asFile)
  override def defaultPublishRepository =
    if (!publishRemote.value) Some(localDestRepo)
    else super.defaultPublishRepository

  lazy val projectInceptionYear       = "2010"

  private lazy val docBottom =
    "Copyright (c) Christos KK Loverdos. " +
      projectInceptionYear + "-" + Calendar.getInstance().get(Calendar.YEAR) +
      ". All Rights Reserved."
}
