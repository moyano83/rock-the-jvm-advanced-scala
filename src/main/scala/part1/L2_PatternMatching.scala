package part1

object L2_PatternMatching extends App {


  val list = List(1)
  list match {
    case head :: Nil => print(s"${head} is the only element")
  }

  /*
     Pattern matching is available with:
     Constants
     Wildcards
     case classes
     tuples
    */

  //We want to make pattern matching available for the class below
  class User(val name: String, val age: Int) {}

  //Define companion object
  object User {
    // Runtime looks for a method unapply in the Person class that returns a tuple with two
    // things until it finds one, if the return type of Option is empty, then the pattern does not match
    def unapply(person: User): Option[(String, Int)] = Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      Some(if (age < 18) "minor" else "major") //unapply patterns can be overloaded
  }

  val bob = new User("Bob", 30)
  bob match {
    case User(name, age) => println(s"Hi, my name is $name and my age is $age")
    case _ => println("Not match")
  }

  // The singleton object that contains the pattern can be called in any way, as long as it is
  // the same in the pattern, i.e. :
  // object PatternPerson{def unapply(...)}
  // bob match{PatternPerson(a,b) => ...}
  val legalStatus = bob.age match {
    case User(status) => s"Bob is ${status}"
    case _ => println("Not match")
  }

  //If we want a custom pattern match depending on certain conditions we can do it like:
  object evenNumber { //pattern match objects are lower case by convention
    def unapply(arg: Int): Option[String] = if (arg % 2 == 0) Some("even") else None
  }

  object doubleDigit {
    def unapply(arg: Int): Option[Boolean] = if (-10 <= arg || arg >= 10) Some(true) else None
  }

  val age = 20

  //The above pattern matches can be replaced by methods returning boolean
  age match {
    case evenNumber(message) => print(message)
    case doubleDigit(_) => print("is double digits")
  }

  //PART 2
  //infix patterns
  case class Or[A, B](a: A, b: B) //This is the scala's Either type

  val bob13: String Or Int = Or("bob", 13)

  bob13 match {
    //Only works for binary patterns
    case name Or age => println("Example of infix patterns") //compiler rewrites this as Or(name, age)
  }

  //decomposing sequences
  abstract class MyList[+A] {
    def head: A = ???

    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]

  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  //Implementation of pattern match for sequences
  // Needs to return a sequence of the elements contained in my collection
  object MyList {
    def unapplySeq[A](arg: MyList[A]): Option[Seq[A]] =
      if (arg == Empty) Some(Seq.empty)
      else unapplySeq(arg.tail).map(arg.head +: _)
  }

  val exampleList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = exampleList match {
    // MyList(1,2,3,...) makes reference to the name of the pattern, not the object to pattern
    // match against. Patterns with varargs are pattern matched against the method unapplySeq
    // (variable patterns)
    case MyList(1, 2, 3, _*) => println("Starting with 1, 2, 3")
    case _ => print("Something else")
  }

  //Custom return types for unapply
  // The data type returned by unapply doesn't need to be an option, it can be your  own type as
  // long as it defines two methods: isEmpty:Boolean and get:Something
  abstract class Wrapper[A] {
    def isEmpty: Boolean

    def get: A
  }

  object PersonWrapper {
    def unapply(user: User): Wrapper[String] = new Wrapper[String] {
      override def isEmpty: Boolean = false

      override def get: String = user.name
    }
  }

  //The above object can be used in pattern match like
  bob match {
    case PersonWrapper(name) => s"This person name is ${name}"
  }
}
