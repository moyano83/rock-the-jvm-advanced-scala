package part4

object OrganizingImplicits extends App {

  // there must be an implicit for ints in the scope to pass to the sorted method
  // This is retrieved from scala.Predef, which is imported by scala by default
  println(List(2, 1, 4, 3).sorted)

  // The implicit define in the code takes precedence over the one imported by default
  // since there is a priority, there is no collision of which implicit to use and it compiles
  implicit val orderingFromGreather: Ordering[Int] = Ordering.fromLessThan(_ > _)
  println(List(2, 1, 4, 3).sorted)

  /* Implicit values (to be used as implicit paramethers) can be:
   * - Val/vars
   * - Objects
   * - accessor methods (def with no parentheses)
   */

  // Exercise
  case class Person(name: String, age: Int)

  implicit def personsOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)

  /* Implicit scope by order
   * - normal scope: Local
   * - imported scope
   * - Companion of all types involved in the method signature
   * -
   */

  /* BEST PRACTICES
   * When defining an implicit val:
   * 1. If there is  single value for it and you can edit the code of the type, then define the implicit in the companion
   * 2. If there are many possible values for it but a single good one and you can edit the code for the type, then define
   *    the good implicit in the companion
   */

  case class Purchase(nUnit: Int, unitPrice: Double)

  //Exercise: implement the implicit ordering by totalPrice(used by 50% of people), by unit count (25%) and unitPrice(25%)
  object Purchase {
    implicit def orderByTotalPrice: Ordering[Purchase] =
      Ordering.fromLessThan((a, b) => (a.nUnit * a.unitPrice) < (b.nUnit * b.unitPrice))
  }

  object OrderByUnit {
    implicit def orderByUnit: Ordering[Purchase] =
      Ordering.fromLessThan((a, b) => a.nUnit < b.nUnit)
  }

  object OrderByAmount {
    implicit def orderByAmount: Ordering[Purchase] =
      Ordering.fromLessThan((a, b) => a.unitPrice < b.unitPrice)
  }

  println(List(Purchase(10,16), Purchase(13,9), Purchase(12,12)).sorted)
}