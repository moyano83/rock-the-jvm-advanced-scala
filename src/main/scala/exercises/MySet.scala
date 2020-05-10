package exercises

sealed trait MySet[A] extends (A => Boolean) {
  override def apply(a: A): Boolean = contains(a)
  def contains(a:A):Boolean
  def +(a:A):MySet[A]
  def ++(mySet: MySet[A]):MySet[A]
  def map[B](f:A=>B):MySet[B]
  def flatMap[B](f:A=>MySet[B]):MySet[B]
  def filter(f:A=>Boolean):MySet[A]
  def foreach(f:A => Unit):Unit
  def -(a:A):MySet[A]
  def --(other:MySet[A]):MySet[A]
  def &(other:MySet[A]):MySet[A]
  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A]{
  override def contains(a: A): Boolean = false

  override def +(a: A): MySet[A] = this

  override def ++(mySet: MySet[A]): MySet[A] = this

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]()

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]()

  override def filter(f: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  override def -(a:A):MySet[A] = this

  override def --(other:MySet[A]):MySet[A] = this

  override def &(other:MySet[A]):MySet[A] = this

  def unary_! : MySet[A] = new PropertySet[A](_ => true)
}

class NonEmpty[A](head:A, tail:MySet[A]) extends MySet[A]{
  override def contains(a: A): Boolean = head == a || tail.contains(a)

  override def +(a: A): MySet[A] = if(contains(a)) this else new NonEmpty[A](a, this)

  override def ++(other: MySet[A]): MySet[A] = tail ++ other + head

  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = tail.flatMap(f) ++ f(head)

  override def filter(f: A => Boolean): MySet[A] =
    if(f(head)) tail.filter(f) + head
    else tail.filter(f)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def -(a: A): MySet[A] =  if(a == head) tail else tail - a + head

  override def --(other:MySet[A]):MySet[A] = {
    if(other.contains(head)) tail -- other
    else  tail -- other + head
  }

  override def &(other:MySet[A]):MySet[A] = filter(other)

  def unary_! : MySet[A] = new PropertySet[A](x => !this.contains(x))
}

class PropertySet[A](predicate: A => Boolean) extends MySet[A]{
  def contains(a:A):Boolean = predicate(a)
  def +(a:A):MySet[A] = new PropertySet[A](x=> predicate(x) || x == a)
  def ++(mySet: MySet[A]):MySet[A]= new PropertySet[A](x => predicate(x) || mySet.contains(x))
  def map[B](f:A=>B):MySet[B] = fail
  def flatMap[B](f:A=>MySet[B]):MySet[B] = fail
  def filter(f:A=>Boolean):MySet[A] = new PropertySet[A](x => predicate(x) & f(x))
  def foreach(f:A => Unit):Unit = fail
  def -(a:A):MySet[A] = filter(x => x != a)
  def --(other:MySet[A]):MySet[A] = filter(!other)
  def &(other:MySet[A]):MySet[A] = filter(other)
  def unary_! : MySet[A] = new PropertySet[A](x => !predicate(x))

  def fail = throw new IllegalArgumentException("Not supported")
}

object MySet {
  def apply[A](a: A*): MySet[A] = {
    def build(a: Seq[A], acc: MySet[A]): MySet[A] = if (a.isEmpty) acc else build(a.tail, acc + a.head)

    build(a.toSeq, new EmptySet[A]())
  }
}