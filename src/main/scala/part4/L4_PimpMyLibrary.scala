package part4

object L4_PimpMyLibrary extends App{
  // Enrichment allows us to extend a class functionality (which we might not have access to) with additional methods and
  // properties. To do that, define an implicit class that takes a single parameter, a value of the type to enrich:
  implicit class RichInt(value:Int){
    def isEven = value % 2 == 0
    def squareRoot = Math.sqrt(value)
  }

  // after defining the above we can directly find the squareRoot of an integer:
  // This is called type enrichment or pimping
  println(2.squareRoot)
  //For memory optimization purposes, a common patter is to make the above implicit, extend AnyVal, but in that case, the
  // constructor parameter must be of type val

  /* General rules to consider when pimping your classes:
   * 1- Keep type enrichment to implicit classes and type classes
   * 2- Avoid implicit defs as much as possible
   * 3- Package implicits clearly, bring into scope only what you need
   * 4- If you need type conversions make them as specific as possible, never with general types
   */
}
