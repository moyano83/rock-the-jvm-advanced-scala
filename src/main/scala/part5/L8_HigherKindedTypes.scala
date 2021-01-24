package part5

object L8_HigherKindedTypes extends App {

  // higher kinded types are a deeper generic types with some unknown type parameter at the deepest level
  trait AHigherKindedType[F[_]] // This is an advanced language feature

  trait MyList[A] {
    def flatMap[B](F: B => A): MyList[B]
  }

  trait MyListOption[A] {
    def flatMap[B](F: B => A): MyListOption[B]
  }

  trait MyListFuture[A] {
    def flatMap[B](F: B => A): MyListFuture[B]
  }

  // we want to Combine List(a,b) x List(1,2) => List(1a, 1b, 2a, 2b)
  // We will need to replicate the below for Option and Future
  def multiply[A, B](a: List[A], b: List[B]): List[(A, B)] = for {
    x <- a
    y <- b
  } yield (x, y)

  // The solution here are the higher kinded types
  trait Monad[F[_], A] {
    def flatMap[B](f: A => F[B]): F[B]

    def map[B](f: A => B): F[B]
  }

  class MonadList[A](list: List[A]) extends Monad[List, A] {
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)

    override def map[B](f: A => B): List[B] = list.map(f)
  }

  //Once we have this in place, we can create a generic multiply for monads
  def multiply[F[_], A, B](monadA: Monad[F, A], monadB: Monad[F, B]): F[(A, B)] = for {
    x <- monadA
    y <- monadB
  } yield (x, y)

  // This is equivalent to monadA.flatMap(x => monadB.map(y => (x,y)))

  println(multiply(new MonadList(List(1, 2, 3)), new MonadList(List("a", "b", "c"))))

  //We can use the above for other monadic types, for example:
  class MonadOption[A](option: Option[A]) extends Monad[Option, A] {
    override def flatMap[B](f: A => Option[B]): Option[B] = option.flatMap(f)

    override def map[B](f: A => B): Option[B] = option.map(f)
  }

  println(multiply(new MonadOption(Option(1)), new MonadOption(Option("a"))))

  // We can also autoconvert the  monad wrapper for any type involved in the multiply:
  implicit class MonadImplicitList[A](list: List[A]) extends Monad[List, A] {
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)

    override def map[B](f: A => B): List[B] = list.map(f)
  }

  //Once we have this in place, we can create a generic multiply for monads
  def multiplyImplicit[F[_], A, B](monadA: Monad[F, A], monadB: Monad[F, B]): F[(A, B)] = for {
    x <- monadA
    y <- monadB
  } yield (x, y)

  println(multiplyImplicit(List(1, 2, 3), List("a", "b", "c"))) //
}
