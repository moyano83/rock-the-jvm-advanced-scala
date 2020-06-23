package part5

object L5_StructuralTypes extends App{

  type JavaCloseable = java.io.Closeable

  class OtherCloseable{
    def close():Unit = println("Other Closeable")
  }

  //We want a method that accepts either the java or otherCloseable classes without duplicating code
  type UnifiedCloseable = {
    def close():Unit
  } //This is a structural type, which might have variables and methods inside
  // now we can do:
  def closeQuitly(v:UnifiedCloseable): Unit = v.close()

  closeQuitly(new OtherCloseable)
  closeQuitly(new JavaCloseable{
    override def close(): Unit = println("Java Closeable")
  })

  // Type Refinement: We can substitute a type alias with a class that provides the implementation for the type alias. i.e.
  type AdvancedCloseable = JavaCloseable {
    def closeSilently():Unit
  }
  //The class below doesn't implement AdvancedCloseable, but it has a method that is equal to the one in the trait
  class AdvancedJavaCloseable extends JavaCloseable{
    override def close(): Unit = println("Advanced Java Closeable")
    def closeSilently():Unit = println("Advanced Java Closeable Silently")
  }
  // The compiler infers that since AdvancedJavaCloseable originates from JavaCloseable, and AdvancedCloseable is an alias
  // for JavaCloseable, then it can replace it.
  val a:AdvancedCloseable = new AdvancedJavaCloseable

  // the following however won't work
  class OtherCloseableExample {
    def close(): Unit = println("Other Closeable close!")
    def closeSilently(): Unit = println("Other Closeable Silently")
  }
  // val a:AdvancedCloseable = new OtherCloseableExample !!Compilation error

  //Structural types can be used as their own types:
  // {def close():Unit} is it's own type, it is an example of an standalone type
  // This is equivalent to the implementation of AdvancedCloseable
  def alternativeClose(closeable:{def close():Unit}):Unit = closeable.close()
  alternativeClose(new OtherCloseable)
  alternativeClose(new OtherCloseableExample)

  // Type Checking -> Duck Typing
  type SoundMaker = {
    def makeSound():Unit
  }
  class Dog {
    def makeSound():Unit = println("Dog Sound")
  }
  class Car {
    def makeSound():Unit = println("Car Sound")
  }

  // The compiler is fine with the below as long as the type on the right hand side conforms with the structure define in the
  // structure on the left hand side. This is called Duck Typing (if it sounds like a duck, can be used as a duck)
  val dog:SoundMaker = new Dog
  val car:SoundMaker = new Car
  // The structural types are based on reflection, that's how the compiler guarantees that the instance defines the
  // behaviour of the type that we want to get. But reflection has a big impact on performance, so use it carefully

  // Exercises:
  // 1 - is f[T] compatible with a Human?
  trait CBL[+T]{
    def head:T
    def tail:CBL[T]
  }

  class Human{
    def head:Brain = new Brain
  }

  class Brain{
    override def toString: String = "BRAIINZ!"
  }

  def f[T](somethingWithHead:{def head:T}) = println(somethingWithHead.head)

  // Response: Human is not CBL because the tail method does not exists, but is compatible with f because it implements
  // the standalone type
  f(new Human) // The compiler infers that T is Brain

  // Is this object below with the CBL and Human?
  object HeadEqualizer{
    type Headable[T] = {def head:T}
    def ===[T](t:Headable[T], b:Headable[T]):Boolean = t.head == b.head
  }

  // Response: HeadEqualizer can accept Human because the Human implements the single method of the type
  HeadEqualizer.===(new Human, new Human)
  // This is not type safe, because the type [T] in HeadEqualizer is Erased and because the type ducking relies on
  // reflection, the above method is reduced to Headable == Headable
}
