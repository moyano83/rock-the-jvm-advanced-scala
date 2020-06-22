package part2

object CurriesAndPAF extends App{

  //curried functions

  //Function taking an int that returns another function taking an Int returning an Int
  val superAdder : Int => Int => Int = x => y => x + y // This is a curried function

  val add3:Int=>Int = superAdder(3) // partially applied function

  add3(5)// 3 => y => 3 + 5

  //you can call the curried function with more parameters
  println(add3(5) == superAdder(3)(5))

  // scala allows the definition of curried methods with multiple parameter list
  def curriedMethod(x:Int)(y:Int):Int = x - y //this is as well a curried method

  //partially  applied functions can be defined by fulfilling one of the parameter lists
  // The function type has to be defined, when supplied only a subset of the parameter lists, the
  // compiler call lift on the curried function, returning a list with fewer parameter list.
  // Beware that the function here is actually a function value and not a method that is part of
  // an instance of a class
  val substractTo4:Int => Int = curriedMethod(4)
  println(substractTo4(5))

  //The compiler creates functions out of methods with a technique called ETA-Expansion
  def increment(x:Int) = x + 1
  // Here ETA-expansion transform def increment in val increment
  List(1,2,3).map(increment)

  // in partial function applications, we force the compiler to do ETA-expansion
  // converts the expression into an Int => Int function after the first parameter list
  val add5:Int => Int  = superAdder(5) (_)

  // you can call the curried method on a function value to get the curried version of that
  // function
  val addTwoNumbers = (x:Int, y:Int) => x + y
  def addTwoNumbersDef = (x:Int, y:Int) => x + y
  val addt2 = addTwoNumbers.curried(3) // returns a function (y:Int) => 3 + y
  val addt3:Int => Int = addTwoNumbers(3, _:Int) // returns another PAF
  val addt3bis:Int => Int = addTwoNumbersDef(3, _:Int) // returns another PAF

  println(s"Methods are the same? ${addt3bis(4) == addt3(4)}")

  //Underscores are powerful
  def concatenator(a:String, b:String, c:String) = a + b + c

  // returns a function value after an ETA -expansion, the order of the underscores are keep in
  // the values passed to the method
  val insertName = concatenator("Hello, ", _, ", how are you?")

  println(insertName("Jorge"))


  /**
    * Exercises
    */

  def byName(n: =>Int):Int = n+1
  def byFunction(f:() => Int):Int = f() + 1

  def method:Int = 42
  def parenMethod():Int = 42

  /*
  calling byName and by function with
  - int
  - method
  - parenMethod
  - lambda
  - PAF
   */


  byName(23)
  byName(method)
  byName(parenMethod())
  byName(parenMethod) // equivalent to byName(parenMethod()) an int value is passed, not a Higher Order Function
  //byName(() => 323)
  byName((() => 323)()) // call the fuction and then pass the value
  // byName(parenMethod _) // cant pass a HOD
  // byFunction(23) //expects a lambda does not work
  // byFunction(method) // the compiler evaluates the function parameter and converts it to a value! it does not do ETA exp
  byFunction(parenMethod) // compiler does ETA exp
  byFunction(()=>23)
  byFunction(parenMethod _)
}
