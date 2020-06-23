package part4
import java.util.Optional
import java.{util => ju}

import scala.util.{Failure, Success, Try}

object L7_ScalaJavaConversions extends App{
  // Sometimes we need to interact with Java classes, which is hard with collections because the scala ones are really
  // different from the Java ones. To do this, we import the following:
  import collection.JavaConverters._

  val javaset: ju.Set[Int] = new ju.HashSet[Int]()
  (1 to 5).foreach(javaset.add)
  println(javaset)

  //Convert the above to scala set
  // This is possible because the compiler search for implicit conversions in the JavaConverters object
  // and finds asScalaSetConverter
  val scalaSet = javaset.asScala

  import collection.mutable._
  //if I convert the scalaSet back to java, we get the same collection reference
  println(javaset eq scalaSet.asJava)
  //The above is not always true:
  val number = List(1,2,3)
  val javaList = number.asJava
  // This is not true because the first list is immutable, but asScala returns a mutable buffer
  println(number eq javaList.asScala)
  println(number == javaList.asScala) // Deep equals is true

  // This fails because javaList is an immutable collection, but the interface in Java allows us to do this operation
  Try(javaList.add(1)) match {
    case Success(x) =>
    case Failure(e) => e.printStackTrace()
  }

  class ToScala[T](v: => T){
    def asScala:T = v
  }
  // Exercise create a Scala/Java Optional to Option class

  implicit def OptionalConversionFromJava[A](v: Optional[A]):ToScala[Option[A]] =
    new ToScala[Option[A]](Option(v.get))
  implicit def OptionalConversionFromScala[A](v: Option[A]):ToScala[Optional[A]] =
    new ToScala[Optional[A]](Optional.ofNullable(v.get))

  val juOptional = ju.Optional.ofNullable(2)
  println(juOptional.asScala)
}
