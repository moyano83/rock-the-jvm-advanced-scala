package part4

object L3_TypeClasses extends App {

  // A Type class is a trait that takes a type and describe which operations can be performed on that class

  /* Imagine we want to implement something like this, for a social network, to print all the objects that are needed in an
   * HTML format
   */
  trait HTMLWritable {
    def toHtml: String
  }

  /* We can put the implementation in every class, but has some disadvantages
   * 1 - This only works for the type we write
   * 2 - This is just an implementation, might be better ones
   */
  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div>${name} ${age} ${email}</div>"
  }

  /* Another approach is to use an object and to pattern matching, but with this approach:
   * 1 - With this approach we lost type safety
   * 2 - Need to modify the code all the type
   * 3 - This is a single implementation
   */
  object HTMLWritable {
    def serializeToHTML(a: Any) = a match {
      case User(a, b, c) =>
      case _ =>
    }
  }

  /* There is a better approach, which is the following: */
  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    override def serialize(u: User): String = s"<div>UserSerializer: ${u.name} ${u.age} ${u.email}</div>"
  }

  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(u: User): String = s"<div>PartialUserSerializer: ${u.name}</div>"
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
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  implicit object UserEqualsByName extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  implicit object UserFullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && b.email == a.email
  }

  // Part2
  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div>${value}</div>"
  }

  //PART 2:
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)

    /* By adding this method, the compiler will surface the entire scope for the type serializer, and would be returned by
     * this method
     */
    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  /* As it can be seen the compiler inserts the appropriate serializer for the type INT
   * we can define multiple implicits for different types, and the compiler will be able to figure out which one to use
   */
  println(HTMLSerializer.serialize(32))
  // This has the same effect, but we have access to the entire type class interface, not only the serialize method
  println(HTMLSerializer[Int].serialize(32))

  /* Implement the Equal interface with this type class

   */
  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean = equalizer.apply(a, b)
  }

  //This is called AD-HOC polymorphism
  println(Equal(User("John", 13, "john@test.com"), User("Pete", 20, "pete@test.com"))(UserEqualsByName))

  //PART 3:
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  // This is where the implicit classes enrichment stands, note that john is implicitly converted to a HTMLEnrichment class
  // and then we can define which serializer to use without touching the original User class, if we define the implicit
  // parameter to toHTML method, it looks like the method is part of the user class
  println(john.toHTML)
  // It is possible to have multiple implementations for the same type
  println(john.toHTML(PartialUserSerializer))
  // Look like if there is another implicit for the type in the scope, it looks like the method is part of the type class
  println(2.toHTML)
  /* The type class pattern composes of:
   * - The type class itself  --> HTMLEnrichment[T]
   * - The type class instances (some are implicit) --> UserSerializer, IntSerializer
   * - Conversion with implicit classes --> HTMLEnrichment
   */

  //Context bounds
  def HTMLBoilerPlate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body>${content.toHTML(serializer)}</body></html>"

  // This is identical to the previous one, but I don't use serializer because we use context bounds, which tell the
  // compiler to inject a HTMLSerializer for the type T
  def HTMLsugar[T: HTMLSerializer](content: T): Unit = s"<html><body>${content.toHTML}</body></html>"

  // The above is possible because of "implicitly" which is a method, which is demonstrated below
  //implicitly
  case class Permissions(mask: String)

  implicit val defaultPermissions = Permissions("444")

  //Imagine that in some other part of the code, we want to check what is the implicit value for permissions
  // We can use the implicitly method:
  val standardPermissions = implicitly[Permissions]

  //Given this, we can express the HTML sugar above like this:
  def HTMLsugar2[T: HTMLSerializer](content: T): Unit = {
    val serializer = implicitly[HTMLSerializer[T]]
    s"<html><body>${content.toHTML(serializer)}</body></html>"
  }

}
