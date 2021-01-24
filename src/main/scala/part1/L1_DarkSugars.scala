package part1

object L1_DarkSugars extends App {

  // # 1: Method with single parameters
  def singleArgMethod(x: Int) = x + 1

  val resultArg = singleArgMethod {
    1 // This is x in the expression
  }

  // # 2: single abstract method pattern
  trait Action {
    def act(x: Int): Int
  }

  val anAction: Action = (x: Int) => x + 1

  abstract class SomeClass {
    def test(x: Int): Unit
  }

  val aSomeClass: SomeClass = (x: Int) => x + 2

  // #3: The :: and ::# are special methods
  val prepend = 2 :: List(1, 2)
  // The above is rewritten to List(1,2).::(2)
  // The associativity of the methods is determined by the last character of the method
  // if it ends in a : is right associative


  // #4: multiword method name

  def `and then said`(x: String) = s"something ${x}"

  this `and then said` "hello"


  // #5: infix types
  class Composite[A, B]

  val compositeValue: Int Composite String = new Composite[Int, String]()

  class -->[A, B]

  val compositeArrow: String --> String = new -->[String, String]()

  // #6: update Method
  val array = Array(1, 2, 3)
  array(2) = 7 // rewrittten as array.update(2,7)

  // # 7: setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0 // encapsulation

    def member = internalMember // getter

    def member_=(x: Int): Unit = internalMember = x
  }

  val theMutable = new Mutable
  theMutable.member = 3 //rewritten as theMutable.member_=(3)

  theMutable.member //getter
}
