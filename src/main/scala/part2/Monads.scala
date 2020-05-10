package part2

object Monads extends App{

  // Try monad
  trait Attempt[+A]{
    def flatMap[B](f: A => Attempt[B]):Attempt[B]
  }

  object Attempt{
    def apply[A](a: => A): Attempt[A] =  try{
      Success(a)
    } catch {
      case ex:Exception => Fail(ex)
    }
  }

  case class Success[A](a:A) extends Attempt[A]{
    def flatMap[B](f: A => Attempt[B]):Attempt[B] = try{
      f(a)
    } catch{
      case ex => Fail(ex)
    }

  }

  case class Fail(e:Throwable) extends Attempt[Nothing]{
    def flatMap[B](f: Nothing => Attempt[B]):Attempt[B] = this
  }



  // Monad Laws

  // left-identity: unit.flatMap(f) = f(x)
  // Attempt(x).flatMap(f) = f(x) //only makes sense for the Success case
  // Success(x).flatMap(f) = f(x) //proved


  // right identity
  // attempt(x).flatMap(unit) = attempt
  // Success(x).flatMap(x => Attempt(x)) = Attempt(x) = Success(x)
  // Fail(_).flatMap(...) = Fail(_) returned by 'this'


  // Associativity
  // attempt.flatMap(f).flatMap(g) =  attempt.flatMap(x => f(x).flatMap(g))
  // Fail(e).flatMap(f).flatMap(g) = fail(e).flatMap(g) = fail(e)
  // Success(x).flatMap(f).flatMap(g) = f(x).flatMap(g) OR fail(ex)
  // Case 1: f(x).flatMap(g) => Success(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g) OR Fail(e)
}
