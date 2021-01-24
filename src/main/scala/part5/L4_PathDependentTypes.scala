package part5

object L4_PathDependentTypes extends App {

  //You can define class objects and types pretty much everywhere with the exception of types
  class Outer {

    class Inner

    object InnerObject

    type InnerType

    def print(i: Inner) = println("inner")
  }

  //Per instant access
  val outer = new Outer
  // we need an instance of outer to get an instance of inner
  // Diferent instances of the wrapper class produces DIFFERENT TYPES of inner classes (note that is types, not instances)
  val inner = new outer.Inner
  val outer2 = new Outer()

  // This would be invalid:
  // val inner:outer.Inner = new outer2.Inner as outer.Inner and outer2.Inner are different types

  //All the inner types has a common supertype: Outer#Inner
  class Outer2 extends Outer {
    def printAnyInner(i: Outer#Inner) = println("Outer#Inner")
  }

  val outerExtended = new Outer2
  outer.print(inner) // This works
  outerExtended.printAnyInner(inner) // This works
  // outerExtended.print(inner) // This won't work


  // Exercise: DB keyed by String or Int, but maybe others as well
  //Implement:
  //def get[ItemType](key:ofTypeItemType):ItemType = ???
  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    override type Key = K
  }

  trait IntItem extends Item[Int]

  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](v: ItemType#Key): ItemType = ???

  //get[StringItem](42) // Does not compile
  get[StringItem]("42") //compiles
}
