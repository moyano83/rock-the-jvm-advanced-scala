package exercises

import scala.annotation.tailrec

object StreamsPlayGround extends App{
// Lazily evaluated single linked STREAM of elements
abstract class MyStream[+A]{
  def isEmpty:Boolean

  def head:A

  def tail:MyStream[A]

  def #::[B>:A](element:B):MyStream[B] // prepend operator

  def ++[B>:A](anotherStream: =>MyStream[B]):MyStream[B] // concatenate two streams

  def foreach(f:A => Unit):Unit

  def map[B](f:A=>B):MyStream[B]

  def flatMap[B](f:A => MyStream[B]):MyStream[B]

  def filter(predicate:A => Boolean): MyStream[A]

  def take(x:Int):MyStream[A]

  def takeAsList(x:Int):List[A]

  @tailrec
  final def toList[B >:A](acc:List[B] = Nil):List[B] = if(isEmpty) acc.reverse else tail.toList(head :: acc)
}

object EmptyStream extends MyStream[Nothing]{
  override def isEmpty: Boolean = true

  override def head: Nothing = throw new NoSuchElementException("Empty Stream")

  override def tail: MyStream[Nothing] = throw new NoSuchElementException("Empty Stream")

  override def #::[B >: Nothing](element: B): MyStream[B] = new Cons[B](element, this)

  override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  override def foreach(f: Nothing => Unit): Unit = ()

  override def map[B](f: Nothing => B): MyStream[B] = this

  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  override def take(x: Int): MyStream[Nothing] = this

  override def takeAsList(x: Int): List[Nothing] = Nil
}

// By name tail!
class Cons[A](hd:A, tl: => MyStream[A]) extends MyStream[A]{
  override def isEmpty: Boolean = false

  override val head: A = hd //overrided as a value to avoid evaluation each time is called

  override lazy val tail: MyStream[A] = tl // avoiding evaluation

  override def #::[B >: A](element: B): MyStream[B] = new Cons[B](element, this)

  // the tail to the cons is passed by name, therefore the exp ++ is also lazily evaluated
  override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons[B](head, tail ++ anotherStream)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def map[B](f: A => B): MyStream[B] = new Cons[B](f(head), tail.map(f)) // tail is lazily evaluated

  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  override def filter(predicate: A => Boolean): MyStream[A] = {
    if(predicate(head)) new Cons(head,  tail.filter(predicate))
    else tail.filter(predicate) // preserve lazy evaluation!
  }

  override def take(n: Int): MyStream[A] = {
    if (n<=0) EmptyStream
    else if(n==1) new Cons(head, EmptyStream)
    else new Cons(head, tail.take(n-1))
  }

  override def takeAsList(n: Int): List[A] = take(n).toList()
}


//generate values like MyStream.from(1)(x=>x + 1)
object MyStream{
  def from[A](start:A)(generator:A => A):MyStream[A] = new Cons(start, MyStream.from(generator(start))(generator))
}

  val naturals:MyStream[Int] = MyStream.from(1)(x => x+1)

  naturals.take(10000).foreach(println)


  /*
  Create fibonacci serie
   */
  def fibonacci(first:Int, second:Int):MyStream[Int] = {
    new Cons[Int](first , new Cons[Int](second, fibonacci(second, first + second)))
  }

  fibonacci(1,1).take(10).toList().foreach(println)

  /*
  Find the prime numbers by eratosthenes sieve method
   */
  def eratosthenes(numbers:MyStream[Int]):MyStream[Int] = {
    if (numbers.isEmpty) numbers
    else new Cons[Int](numbers.head, eratosthenes(numbers.tail.filter(x=> x % numbers.head != 0)))
  }


  println((eratosthenes(MyStream.from(2)(_+1)).take(100)).toList())
}

