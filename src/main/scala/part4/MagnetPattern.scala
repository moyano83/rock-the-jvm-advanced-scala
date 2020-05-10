package part4

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPattern extends App{
  // Magnet pattern tries to solve problems created by method overloading, for example:

  class P2PRequest
  class P2PResponse
  class Serializer[T]
  trait Actor{
    def receive(statusCode:Int):Int
    def receive(response:P2PRequest):Int
    def receive(response:P2PResponse):Int
    def receive[T](response:Serializer[T]):Int
    def receive(response:Future[P2PRequest])
  }

  /* Problem 1: type erasure: we can't write a method like the one below
   *    def receive(response:Future[P2PResponse])
   * Problem 2: lifting doesn't work for all overloads
   *    val receive = receive _ //This won't work
   * Problem 3: Code duplication, most of the method definitions would do the same but with different types
   * Problem 4: Type inference and default arguments, no way to know which default arg to fetch for a method
  */
  //The solution for the problems above is the Message Magnet patter:
  trait MessageMagnet[Result] {
    def apply():Result
  }
  //With the method below we assure that we receive the right type of the Result
  def receive[T](magnet:MessageMagnet[T]) = magnet()

  implicit class fromP2PRequest(request:P2PRequest) extends MessageMagnet[Int]{
    override def apply(): Int = {
      println("Handling P2P Request")
      42
    }
  }

  implicit class fromP2PResponse(request:P2PResponse) extends MessageMagnet[Int]{
    override def apply(): Int = {
      println("Handling P2P Response")
      42
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  //1- no more type erasure problems
  implicit class fromFutureRequest(request:Future[P2PRequest]) extends MessageMagnet[Int]{
    override def apply(): Int = {
      println("Handling P2P Request in future")
      42
    }
  }

  implicit class fromFutureResponse(request:Future[P2PResponse]) extends MessageMagnet[Int]{
    override def apply(): Int = {
      println("Handling P2P Response in future")
      42
    }
  }

  // The compiler looks for implicit conversions before the type is erased
  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))

  //2- Lifting works (with a catch)
  trait MathLib{
    def add1(x:Int):Int = x + 1
    def add1(x:String):Int = x.toInt + 1
  }
  trait AddMagnet{
    def apply():Int
  }

  def add1(magnet:AddMagnet):Int = magnet()

  implicit class addInt(x:Int) extends AddMagnet{
    override def apply(): Int = x + 1
  }
  implicit class addString(x:String) extends AddMagnet{
    override def apply(): Int = x.toInt + 1
  }
  //After re-written the MathLib as a Magnetize, we can rewrite our code as:
  // The catch is that AddMagnet trait is not parameterized, cause otherwise the compiler won't know which one to use
  val add1V = add1 _
  println(add1(3)) // We can lift the add1 function now
  println(add1("2"))

  /* The magnet patter as drawbacks too
   * 1- It is very verbose
   * 2- It is harder to read
   * 3- You can't place default arguments, it has to receive some kind of magnet or a thing that converts to a magnet
   * 4- call by name doesn't work
   */

  //Regarding point 4, this is an example when the magnet pattern doesnt work for calls by name
  class Handler{
    def handle(s: =>String) = {
      println(s)
      println(s)
    }
  }
  //If we magnetize the above, we will have
  trait HandlerMagnet{
    def apply()
  }
  
  def handle(handlerMagnet: HandlerMagnet) = handlerMagnet()

  implicit class stringHandlerToMagnet(s: => String) extends HandlerMagnet{
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffect():String = {
    println("Hello Scala")
    "Scala"
  }

  handle(sideEffect())
  handle{
    println("Hello Scala")
    "Scala" // <-- Only this value is magnetized, therefore the above only appears one time. This is very hard to trace
  }
}
