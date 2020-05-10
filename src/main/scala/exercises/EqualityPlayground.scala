package exercises

import part4.TypeClasses.{Equal, User, UserSerializer}

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

}
