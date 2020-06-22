package part5

object FBoundedPolimorphism extends App{

  // How to force a method in the supertype to accept a current type?
  trait Animal{
    def breed:List[Animal]
  }

  class Cat extends Animal{
    // we want the supertype to return a list of cats, not animals
    override def breed: List[Animal] = List[Cat]()
  }

  // Solution 1: Change the method signature to return the specific type, list is covariant, so we can replace it by a
  // list of dogs, but we need to rely on the programmer to change the signature, is not forced by the compiler
  class Dog extends Animal{
    override def breed: List[Dog] = List[Dog]()
  }

  //Solution 2: parameterize the trait with itself
  trait Animal2[A<:Animal2[A]]{ // This is called recursive type: F-Bounded Polimorphism
    def breed:List[Animal2[A]]
  }

  class Horse extends Animal2[Horse]{
    override def breed:List[Animal2[Horse]] = ???
  }
  // The problem with this is that in theory, we can code something like the class below:
  class Monkey extends Animal2[Horse]{
    override def breed:List[Animal2[Horse]] = ???
  }

  // Solution 3: Force the compiler to make the parameter type to be the same than the class that implements the trait
  // FBounded Polymorphism with self types:
  trait Animal3[A<:Animal3[A]]{self: A => // This is called recursive type: F-Bounded Polimorphism
    def breed:List[Animal3[A]]
  }

  class Frog extends Animal3[Frog]{ // Any other type won't compile
    override def breed:List[Animal3[Frog]] = ???
  }
  // There is a problem with this implementation, once we bring the hierarchy of the class one level down, then FBounded
  // polimorphism doesn't work anymore
  trait Fish extends Animal3[Fish]

  class Cod extends Fish {
    override def breed: List[Animal3[Fish]] = List()
  }
  class Shark extends Fish {
    override def breed: List[Animal3[Fish]] = List(new Cod()) // Implementation is valid, functionally it is wrong
  }

  // Exercise: Try to solve the problem above
  // Solution 4: Use type classes
  trait Animal4
  trait CanBreed[A]{
    def breed(a:A): List[A]
  }
  class Fox extends Animal4
  object Fox{
    implicit object FoxesCanBreed extends CanBreed[Fox]{
      def breed(fox:Fox):List[Fox] = List(new Fox())
    }
  }

  implicit class CanBreedOps[A](animal:A){
    def breed(implicit canBreed: CanBreed[A]): List[A] = canBreed.breed(animal)
  }

  val fox = new Fox
  fox.breed // fox Here is a CanBReedOps[Fox], which receives the FoxesCanBreed object from the companion Fox object

  // How does the compiler signal a possible error to me? Example:
  class Crocodile extends Animal4
  object Crocodile{
    implicit object CrocodilesCanBreed extends CanBreed[Fox]{
      def breed(fox:Fox):List[Fox] = List(new Fox()) // Perfectly compilable code
    }
  }

  // new Crocodile.breed // Error:could not find implicit value for parameter canBreed:
  // part5.FBoundedPolimorphism.CanBreed[part5.FBoundedPolimorphism.Crocodile]

  // Solution 5: pure type classes
  //
  trait Animal5[A]{
    def breeding(a:A):List[A]
  }
  class Rat
  object Rat {
    implicit object RatAnimal extends Animal5 [Rat]{
      override def breeding(a:Rat):List[Rat] = List()
    }
  }
  implicit class AnimalEnrichment[A](value:A){
    def breeding(implicit animalTypeClassInstance:Animal5[A]): List[A] = animalTypeClassInstance.breeding(value)
  }

  val rat = new Rat
  rat.breeding
}
