import io.buildo.enumero._

import org.scalatest.{ Matchers, WordSpec }

class CaseEnumSpec extends WordSpec with Matchers {
  sealed trait Planet extends CaseEnum
  object Planet {
    final class Mercury() extends Planet
    val Mercury: Planet = new Mercury()
    final class Venus() extends Planet
    val Venus: Planet = new Venus()
    final class Earth() extends Planet
    val Earth: Planet = new Earth()
  }

  "CaseEnumMacro" should {
    "construct a sensible CaseEnumSerialization" in {
      val serialization = CaseEnumSerialization.caseEnumSerialization[Planet]

      val pairs = List(
        Planet.Mercury -> "Mercury",
        Planet.Venus -> "Venus",
        Planet.Earth -> "Earth")

      for ((co, str) <- pairs) {
        serialization.caseToString(co).shouldBe(str)
        serialization.caseFromString(str).shouldBe(Some(co))
      }
    }
  }

  "SerializationSupport" should {
    "provide the typeclass instance" in {
      trait FakeJsonSerializer[T] {
        def toString(value: T): String
        def fromString(str: String): Either[String, T]
      }

      implicit def fakeJsonSerializer[T <: CaseEnum](implicit instance: CaseEnumSerialization[T]) = new FakeJsonSerializer[T] {
        def toString(value: T): String = instance.caseToString(value)
        def fromString(str: String): Either[String, T] = instance.caseFromString(str) match {
          case Some(v) => Right(v)
          case None => Left(
            s"$str is not a valid ${instance.name}. Valid values are: ${instance.values.map(_.getClass.getName.split('$').last).mkString(", ")}"
          )
        }
      }

      implicitly[FakeJsonSerializer[Planet]].fromString("Mercury").shouldBe(Right(Planet.Mercury))
      implicitly[FakeJsonSerializer[Planet]].fromString("Wrong").shouldBe(Left(
        "Wrong is not a valid Planet. Valid values are: Mercury, Venus, Earth"
      ))
    }

    "retrieve a typeclass instance using apply" in {
      CaseEnumSerialization[Planet].caseFromString("Mercury") shouldBe Some(Planet.Mercury)
    }
  }

}
