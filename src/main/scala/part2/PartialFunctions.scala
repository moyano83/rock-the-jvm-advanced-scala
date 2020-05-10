package part2

object PartialFunctions extends App{

  val afunction = (x:Int) => x + 1 // function on all the Int domain

  val aFuzzyFunction = (x:Int) => x match {
    case 1 => 12
    case 3 => 42
    case 5 => 111
  }
  //Function defined in the domain {1,3} => Int is a partial function from Int to Int
  //Scala support partial functions like below:
  val aPartialFunction:PartialFunction[Int, Int] = {
    case 1 => 12
    case 3 => 42
    case 5 => 111
  } // this is a partial function value
  //we can call the partial function
  println(aPartialFunction(3))

  //we can check if a partial function is defined for a value
  println(aPartialFunction.isDefinedAt(12)) // return false

  //a partial function can be lifted to a function that returns Option
  println(aPartialFunction.lift(12))

  //partial functions can be chained with another partial function as argument
  aPartialFunction.orElse[Int, Int]{
    case 2 => 123
  }

  // partial functions can be attributed to something declared as a total function
  // because partial functions extends function and therefore higher order functions accepts
  // partial functions
  val aTotalFunction :Int => Int = {
    case 4 => 122
  }
  //Note: A partial function can only have one parameter type!

  //exercise: instantiate a partial function
  val apartialSalutation= new PartialFunction[String, String] {
    override def apply(v1: String): String = s"Hello $v1"

    override def isDefinedAt(x: String): Boolean = x.startsWith("Hi")
  }

  val aPartialFunctionBot:PartialFunction[String, Unit] = {
    case "hello" => println(s"Hi!")
    case "goodbye" => println("Bye")
  }

  //scala.io.Source.stdin.getLines.map(aPartialFunctionBot).foreach(println)

  val aSet = Set(1) //Sets are functions

  //Sequences can be seen as partial functions for the domain (0,lenght -1) as the apply method
  // retrieves a value stored in the index (Int) or throws an exception. Maps are partial
  // functions on the domain of their keys

}
