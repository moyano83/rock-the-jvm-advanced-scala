package part5

object RockingInheritance extends App{
  //convenience
  trait Writer[T]{
    def write(t:T):Unit
  }

  trait Closeable{
    def close(status:Int):Unit
  }

  trait GenericStream[T]{
    def foreach(x:T => Unit):Unit
  }

  //If we have a convenient method that receives something that might mix some other traits we can write
  def processStream[T](stream:GenericStream[T] with Writer[T] with Closeable):Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  //diamon problem with multiple inheritance
  trait Animal {def name:String}
  trait Lion extends Animal {override def name = "Lion"}
  trait Tiger extends Animal{override def name = "Tiger"}
  class Tigron extends Tiger with Lion{
    // The compiler is ok with this because the Tigron overrides both overrides
    // if we remove this, the class still compiles because this is equivalent to
    // Tigron extends animal with Tiger(overrides Animal) with Lion(override Tiger): chain of inheritance from left to right
    override def name = "Tigron"
  }

  //Super problem or type linealization
  trait Cold{
    def print:Unit = println("Cold")
  }

  trait Green extends Cold{
    override def print:Unit = {
      println("Green")
      super.print
    }
  }

  trait Blue extends Cold{
    override def print:Unit = {
      println("Blue")
      super.print
    }
  }

  class Red {
    def print:Unit = println("Red")
  }

  class White extends Red with Green with Blue{
    override def print:Unit = {
      println("White")
      super.print
    }
  }

  /*
   *  The explanation to the above is in the type hierarchy:
   *        Cold
   *          |  \
   *   Red  Blue  Green
   *     \    |    /
   *      \   |   /
   *       \  |  /
   *        White
   *
   * Internally this is resolved as follows:
   * Cold = AnyRef with <Implementation of Cold>
   * Green =
   * Blue = AnyRef with <implementation of Cold> with <Implementation of Blue>
   * Red = AnyRef with <Implementation of Red>
   *
   * White = AnyRef with Red with (AnyRef with <implementation of Cold> with <Implementation of Green>)
   *    with (AnyRef with <implementation of Cold> with <Implementation of Blue>) with <Implementation of White>
   *
   * Given that the compiler does the following: it takes all this elements in turn, and whatever it sees for the
   * second time, it jumps over, therefore the above is simplified as (known as type linearization):
   *
   * White = AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>
   */
  val color = new White();
  color.print

}
