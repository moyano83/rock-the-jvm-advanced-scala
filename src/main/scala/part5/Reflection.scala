package part5


object Reflection extends App {
  // How do I instantiate a class or call a method by passing the name in runtime?
  // reflection + macros + quasiquotes => METAPROGRAMMING

  //PART 1

  case class Person(name:String){
    def sayMyName():Unit = println(s"Hi mi name is ${name}")
  }
  // 0 - Import universe
  import scala.reflect.runtime.universe
  // 1 - get Mirror
  val m = universe.runtimeMirror(getClass.getClassLoader)
  // 2 - Instantiate a class object
  // This is a class symbol or a description of the class
  val clazz = m.staticClass("part5.Reflection.Person") // the type of this class is runtime.universe.ClassSymbol
  // 3 - create a reflected Mirror
  // With the information from clazz we can create a ReflectiveMirror that CAN DO things
  val cm = m.reflectClass(clazz)
  // 4 - Get the constructor
  val constructor = clazz.primaryConstructor.asMethod
  // 5 - Get the reflected Constructor
  val constructorMirror = cm.reflectConstructor(constructor)
  // 6 - invoke the constructor
  val instance = constructorMirror.apply("John") // This is going to return a class Person

  println(instance)

  //  We can also call a method dinamically by name
  val methodName = "sayMyName"
  // 1 - get Mirror
  // 2 - Reflect the instance
  val reflected = m.reflect(instance) // This returns a mirror
  // 3 - Get the method symbol
  val methodSymbol = universe.typeOf[Person].decl(universe.TermName(methodName)).asMethod
  // 4 - Reflect the method
  val method = reflected.reflectMethod(methodSymbol)
  // 5 - Use the method
  method.apply()

  //So the pattern here is to instantiate a mirror first by calling runtime mirror of the class loader
  //and then reflecting whatever it is that you would use for invoking the member at runtime.
  //So in the first case we reflected the class and then in the second example we reflected an instance

  // PART2 : Type Erasure and Reflection
  // Due to type erasure, you cannot differenciate generic types at runtime
  List(1,2) match {
    case _:List[String] =>println("A List of Strings") // This option is printed due to type erasure
    case _:List[Number] =>println("A List of Number")
  }
  // The IDE give us a warning for the above:
  // fruitless type test: a value of type List[Int] cannot also be a List[Number](but still might match its erasure)

  // The type erasure give us some limitation with the overloading too

  // Solution => TypeTags
  //0 - Import
  import universe._

  // This is a way to create the type tag "manually"
  val tTag = typeTag[Person]

  println(tTag.tpe) // This is the fully qualified name for the class
  // TypeTag are use as a type evidence that allows us to inspect generic types
  class MyMap[K,V]

  def getTypeArguments[T](value:T)(implicit tag:TypeTag[T]) = tag.tpe match {
    // `TypeRef(pre, sym, typeArguments)`: `pre` is the prefix of the type reference, `sym` is the symbol
    // referred to by the type reference, and `typeArguments` is a possible empty list of type arguments.
    case TypeRef(_,_,typeArguments) => typeArguments
    case _ => List()
  }

  println(getTypeArguments(new MyMap[String, Int]()))
  // How is that the type of String Int is not erased? The compiler creates the typeTag at compile time which contains the
  // information about the specific types used

  def isSubType[A,B](a:A,b:B)(implicit ta: TypeTag[A], tb:TypeTag[B]):Boolean = ta.tpe <:< tb.tpe

  class Animal
  class Frog extends Animal
  println("Is Frog subtype of animal? " + isSubType(new Frog, new Animal))

  // The type tags can be linked to other reflection tools
  // 3 - Get the method symbol
  val methodSymbolWithTypeTag = typeTag[Person].tpe.decl(universe.TermName(methodName)).asMethod
  // 4 - Reflect the method
  val methodWithTypeTag = reflected.reflectMethod(methodSymbolWithTypeTag)
  // 5 - Use the method
  methodWithTypeTag.apply()
}
