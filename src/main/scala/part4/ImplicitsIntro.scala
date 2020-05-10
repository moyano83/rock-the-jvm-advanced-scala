package part4

import java.time.temporal.TemporalAmount

object ImplicitsIntro extends App {

  case class Person(name:String) {
    def greet = s"Hello ${name}"
  }

  implicit def fromStringToPerson(name:String):Person = Person(name)

  // The compiler looks for anything on the scope that has a greet method, and then looks for implicits that can
  // convert the string into the person class, but only compiles if there is a single implicit to do the conversion
  println("Peter".greet) //println(fromStringToPerson("Peter").greet)

  def increment(x:Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount = 10

  increment(2) // Implicit is bind by the compiler on the search scope, not to mix with default values
}
