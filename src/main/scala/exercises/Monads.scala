package exercises

object Monads extends App{
  /**
    * IMPLEMENT A LAZY[T] MONAD to abstract a computation that will only be computed when needed
    */

  class Lazy[+A](value: =>A){
    def use:A = value
    def flatMap[B](f: (=> A) => Lazy[B]):Lazy[B] = f(value)

    def map[B](f: (=>A) => B):Lazy[B] = flatMap(x => Lazy(f(x)))

    //def flatten(m: Lazy[Lazy[A]]):Lazy[A] = m.flatMap(x => x)

  }

  object Lazy{
    def apply[A](a: => A): Lazy[A] = new Lazy(a)
  }

  val lazyInstance = Lazy {
    println("Hello")
    42
  }

  lazyInstance.flatMap(x =>Lazy {
    println("Inside FlatMap")
    x*10
  })
  /**
    * Exercise 2: Implement a Monad as a combination of unit + map + flatten from a Monad of type unit + flatMap
    */


}
