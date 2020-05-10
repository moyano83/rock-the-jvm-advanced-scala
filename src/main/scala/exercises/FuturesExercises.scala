package exercises

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{Failure, Random, Success}

object FuturesExercises extends App {

  // Write a future that fullfil inmediately a value

  val futInmediately = Future{
    43
  }

  // Futures in sequence
  val futSeqA = Future{
    val sleepValue = Random.nextInt(500)
    Thread.sleep(sleepValue)
    println("Future A")
    sleepValue
  }
  val futSeqB = Future{
    val sleepValue = Random.nextInt(500)
    Thread.sleep(sleepValue)
    println("Future B")
    sleepValue
  }

  def inSequence[A,B](futA:Future[A], futB:Future[B]): Future[B] ={
    futA.flatMap(_ => futB)
  }

  inSequence(futSeqA, futSeqB)

  Thread.sleep(1000)

  // Create a function that returns the first of two futures
  def first[A](fa:Future[A], fb:Future[A]): Future[A] = {
    val promise = Promise[A]()
    fa.onComplete {
      case Success(value) => if (!promise.isCompleted) promise.success(value)
      case Failure(exception) => promise.failure(exception)
    }
    // The above is equivalent to fa.onComplete(promise.tryComplete)
    fb.onComplete {
      case Success(value) => if (!promise.isCompleted) promise.success(value)
      case Failure(exception) => promise.failure(exception)
    }
    promise.future
  }

  // Create a function that returns the last of two futures
  def last[A](fa:Future[A], fb:Future[A]): Future[A] = {
    val promiseFirst = Promise[A]()
    val promiseSecond = Promise[A]()

    fa.onComplete(theTry => if(!promiseFirst.tryComplete(theTry)) promiseSecond.tryComplete(theTry))
    fb.onComplete(theTry => if(!promiseFirst.tryComplete(theTry)) promiseSecond.tryComplete(theTry))

    promiseSecond.future
  }

  // Given a future, retry until certain condition is meet
  def retryUntil[A](action:()=> Future[A], condition: A => Boolean): Future[A] =
    action().filter(condition).recoverWith{ case _ =>  retryUntil(action, condition) }
}
