package part4

object TypeClasses extends App{
  // A Type class is a trait that takes a type and describe which operations can be performed on that class

  /* Imagine we want to implement something like this, for a social network, to print all the objects that are needed in an
   * HTML format
   */
  trait HTMLWritable{
    def toHTML:String
  }

  /* We can put the implementation in every class, but has some disatvantages
   * 1 - This only works for the type we write
   * 2 - This is just an implementation, might be better ones
   */
  case class User(name:String, age:Int, email:String) extends HTMLWritable{
    override def toHTML: String = s"<div>${name} ${age} ${email}</div>"
  }

  /* Another approach is to use an object and to pattern matching, but with this approach:
   * 1 - With this approach we lost type safety
   * 2 - Need to modify the code all the type
   * 3 - This is a single implementation
   */
  object HTMLWritable {
    def serializeToHTML(a:Any) = a match {
      case User(a,b,c) =>
      case _ =>
    }
  }

  /* There is a better approach, which is the following: */
  trait HTMLSerializer[T]{
    def serialize(value:T):String
  }

  object UserSerializer extends HTMLSerializer[User]{
    override def serialize(u:User): String = s"<div>${u.name} ${u.age} ${u.email}</div>"
  }

  val john = User("John", 30, "john@test.com")
  println(UserSerializer.serialize(john))

  /* With the above example we can:
   * 1- Define serializers for other types as well
   * 2- We can define multiple serializers for the same type by inheritance
   *
   * The above is known as TYPE CLASS. A type class specifies a set of operation that can be applied to a given type
   */

  // Exercise: Implement an equal type class that has a method equals that compares to values. Implement two instances that
  // compare to users by name and age
  trait Equal[T]{
    def apply(a:T, b:T):Boolean
  }

  object UserEqualsByName extends Equal[User]{
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  implicit object UserFullEquality extends Equal[User]{
    override def apply(a: User, b: User): Boolean = a.name == b.name && b.email == a.email
  }

  // Part2

  implicit object IntSerializer extends HTMLSerializer[Int]{
    override def serialize(value: Int): String = s"<div>${value}</div>"
  }

  //PART 2:
  object HTMLSerializer {
    def serialize[T](value:T)(implicit serializer: HTMLSerializer[T]):String = serializer.serialize(value)

    /* By adding this method, the compiler will surface the entiere scope for the type serializer, and would be returned by
     * this method
     */
    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  /* As it can be seen the compiler inserts the appropiate serializer for the type INT
   * we can define multiple implicits for different types, and the compiler will be able to figure out which one to use
   */
  println(HTMLSerializer.serialize(32))
  // This has the same effect, but we have access to the entire type class interface, not only the serialize method
  println(HTMLSerializer[Int].serialize(32))

  /* Implement the Equal interface with this type class

   */
  object Equal{
    def apply[T](a:T,b:T)(implicit equalizer: Equal[T]):Boolean = equalizer.apply(a,b)
  }

  println(Equal(User("John",13,"john@test.com"),User("Pete",20,"pete@test.com"))) //This is called AD-HOC polymorphism



  //PART 3:

}
