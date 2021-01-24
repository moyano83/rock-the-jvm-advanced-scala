package part2

object L3_LazyEvaluation extends App {

  //Lazy  DELAYS the evaluation of values
  lazy val x:Int = throw new RuntimeException("Ex")

  def sideEfectCondition():Boolean = {
    println("Boo")
    true
  }

  def simpleCondition()=false

  // Side effects (not called)
  lazy val side = sideEfectCondition()
  if(simpleCondition() && sideEfectCondition()) println("yes") else println("no")

  // in conjunction with call by name
  def byNameMethod(n: => Int): Int = n + n + n
  def retrieveMagicValue:Int={
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retrieveMagicValue)) //waits 3 seconds
  //use lazy vals instead:
  def byNameMethod2(x: => Int):Int = {
    lazy val t = x //only evaluated once
    t + t + t
  }
  //above technique is called CALL BY NEED

  // filtering with lazy val
  def lessThanThirty(x:Int): Boolean = {
    println(s"${x} is less than 30?")
    x < 30
  }

  def greatherThanTwenty(x:Int):Boolean = {
    println(s"Is ${x} greater than 20?")
    x > 20
  }

  val numbers = List(1, 25,  40, 5, 23)

  val lt30 = numbers.filter(lessThanThirty)
  val gt20 = lt30.filter(greatherThanTwenty)

  println(gt20)

  println("With Filter")
  val lt30Lazy = numbers.withFilter(lessThanThirty)
  val gt20Lazy = lt30Lazy.withFilter(greatherThanTwenty)


  println(gt20Lazy) // prints the type of collection:Lazy Monadic
  gt20Lazy.foreach(println) // evaluates both functions for each number!


  // for comprehensions use withFilter with guards
  for{
    a <-  List(1,2,3,4) if a % 2 == 0 //if guards use lazy vals!
  }yield a + 1

  //The above is translated to List(1,2,3,4).withFilter(_ % 2 == 0).map(_ + 1)


}
