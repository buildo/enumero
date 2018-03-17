import mill._
import mill.scalalib._

val V = new {
  val scala211 = "2.11.11"
  val scala212 = "2.12.4"
}

trait CommonModule extends CrossSbtModule {
  def organization = "io.buildo"
  def scalaVersion = V.scala212
}

object core extends Cross[CoreModule](V.scala211, V.scala212)

class CoreModule(val crossScalaVersion: String) extends CommonModule {

  private val macroparadise = ivy"org.scalamacros:::paradise:2.1.0"

  def ivyDeps = Agg(
    ivy"org.scala-lang:scala-reflect:${crossScalaVersion}"
  )
  def scalacPluginIvyDeps = Agg(macroparadise)

  object test extends Tests {
    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:3.0.1",
      ivy"org.mockito:mockito-all:1.9.5",
    )
    def scalacPluginIvyDeps = Agg(macroparadise)
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}

object circeSupport extends Cross[CirceModule](V.scala211, V.scala212)

class CirceModule(val crossScalaVersion: String) extends CommonModule {
  def moduleDeps = Seq(new CoreModule(crossScalaVersion))
  def ivyDeps = Agg(
    ivy"io.circe::circe-core:0.9.0"
  )
}

