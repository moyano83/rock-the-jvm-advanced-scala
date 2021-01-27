package part5

object L2_Variance extends App {

  trait Animal {
    def name: String
  }

  class Dog(val name: String = "Dog") extends Animal

  class Cat(val name: String = "Cat") extends Animal

  class Kitty(override val name: String = "Kitty") extends Cat

  class Crocodile(val name: String = "Crocodile") extends Animal

  // Variance is the problem of type substitution on generics:
  class Cage[T] {}

  class CCage[+T]

  class XCage[-T]

  /* Should Cage[Cat] inherit from Cage[Animal]? depending on the answer we have:
   * 1 -YES: Covariance
   */
  val cageCat: CCage[Animal] = new CCage[Cat]
  // 2 -NO: Invariant
  //val cageCat:Cage[Animal] = new Cage[Cat] <--Does not compile

  // 3- NO, should be the opposite: Contravariance
  val veterinarie: XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](animal: Animal) //invariant

  // Covariant positions: The generic type of field animal is in a covariant position, in this position the compiler
  // accepts a type defined with a covariant type
  class CovariantCage[+T](val animal: T)

  // The following will not compile:
  // class ContravariantCage[-T](val animal: T)
  // because if the compiler pass this code as valid, you'll be able to code something like this:
  // val catCage: ContravariantCage[Cat] = new ContravariantCage[Animal](new Dog)
  // Dog is an animal, therefore I can pass it as a Dog is an Animal
  //
  // If a covariant type T occurs in contravariant position like the one below:
  // class CovariantVariableCage[+T](var animal:T)
  // This code won't compile either because you'll be able to write something like
  // val cCage: CCage[Animal] = new CCage[Cat](new Cat)
  // cCage.animal = new Dog //Since  animal is a var we can replace it, and as it is defined as Animal, we can pass a Dog
  //
  // Same error happens if we have a:
  // class ContravariantCage[-T](var animal: T)
  // as we would be able to do:
  // val cCage: CCage[Cat] = new CCage[Animal](new Crocodile)
  //
  //   class InvariantCage[T](val animal: Animal) or   class InvariantCage[T](var animal: Animal) // compiles fine

  // trait AnotherCovariantCage[+T]{
  //   def addAnimal(animal:T)
  // }
  // This is also wrong we could do something like:
  // val ccage:AnotherCovariantCage[Animal] = new AnotherCovariantCage[Cat]()
  // ccage.addAnimal(new Dog) //Dog is an animal

  class AnotherContraCovariantCage[-T] {
    def addAnimal(animal: T) = {} //This is fine
  }

  val ccage: AnotherContraCovariantCage[Cat] = new AnotherContraCovariantCage[Animal]()

  // ccage.addAnimal(new Animal) // won't compile
  //ccage.addAnimal(new Cat) // Only cats are allowed

  // If we want to create a covariant collection in which we can add elements to it, we need to do the following:
  class MyList[+A] {
    // def add(element:A):MyList[A] This won't compile
    def add[B >: A](element: B): MyList[B] = new MyList[B] //Compiles because we are widening the type
  }

  val aListOfAnimals = new MyList[Kitty]()
  val animalList1 = aListOfAnimals.add(new Kitty()) // we can pass something that is bounded by cat
  val animalList2 = aListOfAnimals.add(new Cat()) // Cat is a suppertype of Kitty, result in MyList[Cat]
  val animalList3 = aListOfAnimals.add(new Dog()) // Dog is a suppertype of Kitty, results in MyList[Animal]

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION!!

  // Return types
  // Method return types are in covariant position

  // class PetShop[-T]{
  //  def get(isAPuppy: Boolean):T
  // }
  // With the above we can do:
  // val catShop = new PetShop[Animal]
  // val dogShop = catShop //Valid replacement
  // dogShop.get(true) !! Returns a Cat since the implementation of PetShop[Animal]
  // The hack for the above to compile is the following:
  class PetShop[-T] {
    def get[B <: T](defaultAnimal: B): B = defaultAnimal
  }

  val shop: PetShop[Cat] = new PetShop[Animal]
  //shop.get(new Dog) // This call is illegal since Dog is not a subtype of cat
  val kitty = shop.get(new Kitty()) // This will return a Kitty, which still is a Cat

  /*
   * IMPORTANT RULES:
   *  1 - Method arguments are in ContraVariant Position
   *  2 - Return types are in Covariant Position
   */
}
