package exercises

import part4.L3_TypeClasses.{Equal, User, UserSerializer}

object EqualityPlayground {

  trait Equal[T]{
    def apply(a:T, b:T):Boolean
  }

  object Equal{
    def apply[T](a:T,b:T)(implicit equalizer: Equal[T]):Boolean = equalizer.apply(a,b)
  }

  object UserEqualsByName extends Equal[User]{
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  implicit object UserFullEquality extends Equal[User]{
    override def apply(a: User, b: User): Boolean = a.name == b.name && b.email == a.email
  }

  val john = User("John", 30, "john@test.com")
  val pete = User("Pete",20,"pete@test.com")
  println(Equal(john,pete))

  /* Exercise:
   * Improve the Equal TC with an implicit conversion class: ===(anotherValue:T)
   * Add !== (anotherValue:T)
   */

  implicit class EnricherEqual[T](value: T){
    def ===(anotherValue:T)(implicit equality:Equal[T]):Boolean = equality(value, anotherValue)
    def !==(anotherValue:T)(implicit equality:Equal[T]):Boolean = !equality(value, anotherValue)
  }

  println(john === pete)
}
