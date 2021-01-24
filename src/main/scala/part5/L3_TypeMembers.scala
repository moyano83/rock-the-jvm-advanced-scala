package part5

object L3_TypeMembers extends App {

  class Animal

  class Dog extends Animal

  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal
    type CAnimal = Cat
  }

  val ac = new AnimalCollection
  val dog: ac.AnimalType = ???
  val cat: ac.CAnimal = new Cat
  val pup: ac.SuperBoundedAnimal = new Dog

  //Type alias works outside the class
  type anotherCat = ac.CAnimal

  // Type alias are used in interfaces that looks like generic,
  //Something like trait MyList[T] could be defined as:
  trait MyList {
    type T

    def add(t: T): MyList
  }

  // The above can be extended and overrided
  trait MyListInt extends MyList {
    override type T = Int

    def add(t: Int): MyList = ???
  }

  // The dot type: We can use some values type as a type alias
  type CatsType = cat.type
  val newCat: CatsType = cat

  // But the following won't work
  // The compiler won't be able to find if newCat is constructable and what type of parameters it needs

  // Exercise:Enforce a type to be applicable to some types only
  //Consider that the following trait is lock because it was implemented by someone else
  trait MList {
    type A

    def head(): A

    def tail(): MList
  }

  // You want to extend this so only type Int is allowed
  //This shouldn't compile
  class StringMList(head: String, stringMList: StringMList) extends MList {
    type A = String

    def head(): String = head

    def tail(): MList = stringMList
  }

  // And this should
  class IntMList(head: Int, intMList: IntMList) extends MList {
    type A = Int

    def head(): Int = head

    def tail(): IntMList = intMList
  }

  // Solution
  // Create a trait and override the type
  trait ApplicableToNumbers {
    type A <: Number
  }

  //And then Mix it with the class above. The below won't compile
  //class StringMList2(head:String, stringMList: StringMList) extends MList with ApplicableToNumbers {
  //  type A = String
  //  def head():String = head
  //  def tail():MList = stringMList
  //}


}
